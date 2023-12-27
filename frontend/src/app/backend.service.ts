import {map, tap} from 'rxjs/operators';
import {Observable, of} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {
  AddPhraseRequest,
  AddPhraseResponse, ApplyExamResultRequest, CreateCollectionRequest, CreateCollectionResponse, GenerateExamRequest, GenerateExamResponse,
  LoadCollectionRequest,
  LoadCollectionResponse,
  LoadCollectionsResponse,
  LoadPhraseRequest,
  LoadPhraseResponse, RemovePhraseRequest, RemovePhraseResponse, UpdateCollectionRequest, UpdateCollectionResponse
} from '../proto/requests_pb';
import {CurrentCollectionService} from './current-collection.service';
import {CollectionView, Phrase} from '../proto/phrase_pb';
import {Language, LanguageMap} from '../proto/language_pb';
import {TaskResult} from '../proto/exam_pb';


@Injectable({
  providedIn: 'root'
})
export class BackendService {

  private static readonly DEFAULT_HTTP_HEADERS = new HttpHeaders({
    'Content-Type': 'application/x-protobuf',
  });
  private static readonly DEFAULT_REQUEST_OPTIONS = {
    headers: BackendService.DEFAULT_HTTP_HEADERS,
    responseType: 'arraybuffer' as 'arraybuffer',
  };

  constructor(private http: HttpClient, private collectionService: CurrentCollectionService) {}

  loadCollections(): Observable<LoadCollectionsResponse> {
    return this.http.post('/api/collection/loadall', '',
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => LoadCollectionsResponse.deserializeBinary(new Uint8Array(data))));
  }

  maybeLoadCollection(name: string): Observable<CollectionView> {
    if (this.collectionService.isLoaded(name)) {
      return of(this.collectionService.get());
    }
    return this.loadCollection(name).pipe(map(response => <CollectionView>response.getCollection()));
  }

  loadCollection(name: string): Observable<LoadCollectionResponse> {
    const req = new LoadCollectionRequest();
    req.setName(name);
    return this.http.post('/api/collection/load', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => LoadCollectionResponse.deserializeBinary(new Uint8Array(data))),
      tap(response => this.collectionService.update(<CollectionView>response.getCollection())));
  }

  loadPhrase(id: string, collectionName: string): Observable<LoadPhraseResponse> {
    const req = new LoadPhraseRequest();
    req.setCollectionName(collectionName);
    req.setId(id);
    return this.http.post('/api/collection/loadphrase', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => LoadPhraseResponse.deserializeBinary(new Uint8Array(data))));
  }

  updateDiscover(collectionName: string,
                 isDiscoveryEnabled: boolean, discoverPoints: number): Observable<UpdateCollectionResponse> {
    const req = new UpdateCollectionRequest();
    req.setName(collectionName);
    req.setIsDiscoverEnabled(isDiscoveryEnabled);
    req.setDiscoverPoints(discoverPoints);
    return this.http.post('/api/collection/update', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => UpdateCollectionResponse.deserializeBinary(new Uint8Array(data))),
      tap(response => this.collectionService.update(<CollectionView>response.getCollection())));
  }

  addPhrase(collectionName: string, phrase: Phrase): Observable<AddPhraseResponse> {
    const req = new AddPhraseRequest();
    req.setCollectionName(collectionName);
    req.setPhrase(phrase);
    return this.http.post('/api/collection/addphrase', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => AddPhraseResponse.deserializeBinary(new Uint8Array(data))),
      tap(response => this.collectionService.update(<CollectionView>response.getCollection())));
  }

  removePhrase(collectionName: string, phraseId: string): Observable<RemovePhraseResponse> {
    const req = new RemovePhraseRequest();
    req.setCollectionName(collectionName);
    req.setId(phraseId);
    return this.http.post('/api/collection/removephrase', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => RemovePhraseResponse.deserializeBinary(new Uint8Array(data))),
      tap(response => this.collectionService.update(<CollectionView>response.getCollection())));
  }

  createCollection(
    name: string,
    srcLang: LanguageMap[keyof LanguageMap],
    translationLang: LanguageMap[keyof LanguageMap]): Observable<CreateCollectionResponse> {
    const req = new CreateCollectionRequest();
    req.setName(name);
    req.setSourceLanguage(srcLang);
    req.setTranslationLanguage(translationLang);
    req.setDiscoverPoints(500);
    return this.http.post('/api/collection/create', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => CreateCollectionResponse.deserializeBinary(new Uint8Array(data))),
      tap(response => this.collectionService.update(<CollectionView>response.getCollection())));
  }

  generateExam(collectionName: string, timezone: string): Observable<GenerateExamResponse> {
    const req = new GenerateExamRequest();
    req.setCollectionName(collectionName);
    req.setTimezone(timezone);
    return this.http.post('/api/exam/generate', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => GenerateExamResponse.deserializeBinary(new Uint8Array(data))));
  }

  applyExam(collectionName: string, timezone: string, results: Array<TaskResult>): Observable<GenerateExamResponse> {
    const req = new ApplyExamResultRequest();
    req.setCollectionName(collectionName);
    req.setTimezone(timezone);
    req.setTaskResultsList(results);
    return this.http.post('/api/exam/apply', req.serializeBinary().buffer,
      BackendService.DEFAULT_REQUEST_OPTIONS).pipe(
      map((data) => GenerateExamResponse.deserializeBinary(new Uint8Array(data))),
      tap(unused => this.collectionService.collection = new CollectionView()));
  }
}
