// package: micromova.proto
// file: requests.proto

import * as jspb from "google-protobuf";
import * as exam_pb from "./exam_pb";
import * as language_pb from "./language_pb";
import * as phrase_pb from "./phrase_pb";

export class CreateCollectionRequest extends jspb.Message {
  getName(): string;
  setName(value: string): void;

  getSourceLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setSourceLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  getTranslationLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setTranslationLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  getDiscoverPoints(): number;
  setDiscoverPoints(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CreateCollectionRequest.AsObject;
  static toObject(includeInstance: boolean, msg: CreateCollectionRequest): CreateCollectionRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CreateCollectionRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CreateCollectionRequest;
  static deserializeBinaryFromReader(message: CreateCollectionRequest, reader: jspb.BinaryReader): CreateCollectionRequest;
}

export namespace CreateCollectionRequest {
  export type AsObject = {
    name: string,
    sourceLanguage: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    translationLanguage: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    discoverPoints: number,
  }
}

export class CreateCollectionResponse extends jspb.Message {
  hasCollection(): boolean;
  clearCollection(): void;
  getCollection(): phrase_pb.CollectionView | undefined;
  setCollection(value?: phrase_pb.CollectionView): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CreateCollectionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: CreateCollectionResponse): CreateCollectionResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CreateCollectionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CreateCollectionResponse;
  static deserializeBinaryFromReader(message: CreateCollectionResponse, reader: jspb.BinaryReader): CreateCollectionResponse;
}

export namespace CreateCollectionResponse {
  export type AsObject = {
    collection?: phrase_pb.CollectionView.AsObject,
  }
}

export class GenerateExamRequest extends jspb.Message {
  getCollectionName(): string;
  setCollectionName(value: string): void;

  getTimezone(): string;
  setTimezone(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GenerateExamRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GenerateExamRequest): GenerateExamRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: GenerateExamRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GenerateExamRequest;
  static deserializeBinaryFromReader(message: GenerateExamRequest, reader: jspb.BinaryReader): GenerateExamRequest;
}

export namespace GenerateExamRequest {
  export type AsObject = {
    collectionName: string,
    timezone: string,
  }
}

export class GenerateExamResponse extends jspb.Message {
  hasExam(): boolean;
  clearExam(): void;
  getExam(): exam_pb.Exam | undefined;
  setExam(value?: exam_pb.Exam): void;

  getTodayScore(): number;
  setTodayScore(value: number): void;

  getTodayDiscoveredPhrases(): number;
  setTodayDiscoveredPhrases(value: number): void;

  getWeekDiscoveredPhrases(): number;
  setWeekDiscoveredPhrases(value: number): void;

  getStarScoreMillis(): number;
  setStarScoreMillis(value: number): void;

  getTotalOpenedPhrases(): number;
  setTotalOpenedPhrases(value: number): void;

  getTotalUndiscoveredPhrases(): number;
  setTotalUndiscoveredPhrases(value: number): void;

  getUniquePhrasesToday(): number;
  setUniquePhrasesToday(value: number): void;

  getUniquePhrasesWeek(): number;
  setUniquePhrasesWeek(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GenerateExamResponse.AsObject;
  static toObject(includeInstance: boolean, msg: GenerateExamResponse): GenerateExamResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: GenerateExamResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GenerateExamResponse;
  static deserializeBinaryFromReader(message: GenerateExamResponse, reader: jspb.BinaryReader): GenerateExamResponse;
}

export namespace GenerateExamResponse {
  export type AsObject = {
    exam?: exam_pb.Exam.AsObject,
    todayScore: number,
    todayDiscoveredPhrases: number,
    weekDiscoveredPhrases: number,
    starScoreMillis: number,
    totalOpenedPhrases: number,
    totalUndiscoveredPhrases: number,
    uniquePhrasesToday: number,
    uniquePhrasesWeek: number,
  }
}

export class ApplyExamResultRequest extends jspb.Message {
  clearTaskResultsList(): void;
  getTaskResultsList(): Array<exam_pb.TaskResult>;
  setTaskResultsList(value: Array<exam_pb.TaskResult>): void;
  addTaskResults(value?: exam_pb.TaskResult, index?: number): exam_pb.TaskResult;

  getCollectionName(): string;
  setCollectionName(value: string): void;

  getTimezone(): string;
  setTimezone(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ApplyExamResultRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ApplyExamResultRequest): ApplyExamResultRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ApplyExamResultRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ApplyExamResultRequest;
  static deserializeBinaryFromReader(message: ApplyExamResultRequest, reader: jspb.BinaryReader): ApplyExamResultRequest;
}

export namespace ApplyExamResultRequest {
  export type AsObject = {
    taskResultsList: Array<exam_pb.TaskResult.AsObject>,
    collectionName: string,
    timezone: string,
  }
}

export class AddPhraseRequest extends jspb.Message {
  hasPhrase(): boolean;
  clearPhrase(): void;
  getPhrase(): phrase_pb.Phrase | undefined;
  setPhrase(value?: phrase_pb.Phrase): void;

  getCollectionName(): string;
  setCollectionName(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): AddPhraseRequest.AsObject;
  static toObject(includeInstance: boolean, msg: AddPhraseRequest): AddPhraseRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: AddPhraseRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): AddPhraseRequest;
  static deserializeBinaryFromReader(message: AddPhraseRequest, reader: jspb.BinaryReader): AddPhraseRequest;
}

export namespace AddPhraseRequest {
  export type AsObject = {
    phrase?: phrase_pb.Phrase.AsObject,
    collectionName: string,
  }
}

export class AddPhraseResponse extends jspb.Message {
  hasCollection(): boolean;
  clearCollection(): void;
  getCollection(): phrase_pb.CollectionView | undefined;
  setCollection(value?: phrase_pb.CollectionView): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): AddPhraseResponse.AsObject;
  static toObject(includeInstance: boolean, msg: AddPhraseResponse): AddPhraseResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: AddPhraseResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): AddPhraseResponse;
  static deserializeBinaryFromReader(message: AddPhraseResponse, reader: jspb.BinaryReader): AddPhraseResponse;
}

export namespace AddPhraseResponse {
  export type AsObject = {
    collection?: phrase_pb.CollectionView.AsObject,
  }
}

export class LoadCollectionRequest extends jspb.Message {
  getName(): string;
  setName(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LoadCollectionRequest.AsObject;
  static toObject(includeInstance: boolean, msg: LoadCollectionRequest): LoadCollectionRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: LoadCollectionRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LoadCollectionRequest;
  static deserializeBinaryFromReader(message: LoadCollectionRequest, reader: jspb.BinaryReader): LoadCollectionRequest;
}

export namespace LoadCollectionRequest {
  export type AsObject = {
    name: string,
  }
}

export class LoadCollectionResponse extends jspb.Message {
  hasCollection(): boolean;
  clearCollection(): void;
  getCollection(): phrase_pb.CollectionView | undefined;
  setCollection(value?: phrase_pb.CollectionView): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LoadCollectionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: LoadCollectionResponse): LoadCollectionResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: LoadCollectionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LoadCollectionResponse;
  static deserializeBinaryFromReader(message: LoadCollectionResponse, reader: jspb.BinaryReader): LoadCollectionResponse;
}

export namespace LoadCollectionResponse {
  export type AsObject = {
    collection?: phrase_pb.CollectionView.AsObject,
  }
}

export class LoadCollectionsRequest extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LoadCollectionsRequest.AsObject;
  static toObject(includeInstance: boolean, msg: LoadCollectionsRequest): LoadCollectionsRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: LoadCollectionsRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LoadCollectionsRequest;
  static deserializeBinaryFromReader(message: LoadCollectionsRequest, reader: jspb.BinaryReader): LoadCollectionsRequest;
}

export namespace LoadCollectionsRequest {
  export type AsObject = {
  }
}

export class LoadCollectionsResponse extends jspb.Message {
  clearNamesList(): void;
  getNamesList(): Array<string>;
  setNamesList(value: Array<string>): void;
  addNames(value: string, index?: number): string;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LoadCollectionsResponse.AsObject;
  static toObject(includeInstance: boolean, msg: LoadCollectionsResponse): LoadCollectionsResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: LoadCollectionsResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LoadCollectionsResponse;
  static deserializeBinaryFromReader(message: LoadCollectionsResponse, reader: jspb.BinaryReader): LoadCollectionsResponse;
}

export namespace LoadCollectionsResponse {
  export type AsObject = {
    namesList: Array<string>,
  }
}

export class UpdateCollectionRequest extends jspb.Message {
  getName(): string;
  setName(value: string): void;

  getIsDiscoverEnabled(): boolean;
  setIsDiscoverEnabled(value: boolean): void;

  getDiscoverPoints(): number;
  setDiscoverPoints(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UpdateCollectionRequest.AsObject;
  static toObject(includeInstance: boolean, msg: UpdateCollectionRequest): UpdateCollectionRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: UpdateCollectionRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UpdateCollectionRequest;
  static deserializeBinaryFromReader(message: UpdateCollectionRequest, reader: jspb.BinaryReader): UpdateCollectionRequest;
}

export namespace UpdateCollectionRequest {
  export type AsObject = {
    name: string,
    isDiscoverEnabled: boolean,
    discoverPoints: number,
  }
}

export class UpdateCollectionResponse extends jspb.Message {
  hasCollection(): boolean;
  clearCollection(): void;
  getCollection(): phrase_pb.CollectionView | undefined;
  setCollection(value?: phrase_pb.CollectionView): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UpdateCollectionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: UpdateCollectionResponse): UpdateCollectionResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: UpdateCollectionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UpdateCollectionResponse;
  static deserializeBinaryFromReader(message: UpdateCollectionResponse, reader: jspb.BinaryReader): UpdateCollectionResponse;
}

export namespace UpdateCollectionResponse {
  export type AsObject = {
    collection?: phrase_pb.CollectionView.AsObject,
  }
}

export class LoadPhraseRequest extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getCollectionName(): string;
  setCollectionName(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LoadPhraseRequest.AsObject;
  static toObject(includeInstance: boolean, msg: LoadPhraseRequest): LoadPhraseRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: LoadPhraseRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LoadPhraseRequest;
  static deserializeBinaryFromReader(message: LoadPhraseRequest, reader: jspb.BinaryReader): LoadPhraseRequest;
}

export namespace LoadPhraseRequest {
  export type AsObject = {
    id: string,
    collectionName: string,
  }
}

export class LoadPhraseResponse extends jspb.Message {
  hasPhrase(): boolean;
  clearPhrase(): void;
  getPhrase(): phrase_pb.Phrase | undefined;
  setPhrase(value?: phrase_pb.Phrase): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LoadPhraseResponse.AsObject;
  static toObject(includeInstance: boolean, msg: LoadPhraseResponse): LoadPhraseResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: LoadPhraseResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LoadPhraseResponse;
  static deserializeBinaryFromReader(message: LoadPhraseResponse, reader: jspb.BinaryReader): LoadPhraseResponse;
}

export namespace LoadPhraseResponse {
  export type AsObject = {
    phrase?: phrase_pb.Phrase.AsObject,
  }
}

export class RemovePhraseRequest extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getCollectionName(): string;
  setCollectionName(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RemovePhraseRequest.AsObject;
  static toObject(includeInstance: boolean, msg: RemovePhraseRequest): RemovePhraseRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RemovePhraseRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RemovePhraseRequest;
  static deserializeBinaryFromReader(message: RemovePhraseRequest, reader: jspb.BinaryReader): RemovePhraseRequest;
}

export namespace RemovePhraseRequest {
  export type AsObject = {
    id: string,
    collectionName: string,
  }
}

export class RemovePhraseResponse extends jspb.Message {
  hasCollection(): boolean;
  clearCollection(): void;
  getCollection(): phrase_pb.CollectionView | undefined;
  setCollection(value?: phrase_pb.CollectionView): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RemovePhraseResponse.AsObject;
  static toObject(includeInstance: boolean, msg: RemovePhraseResponse): RemovePhraseResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RemovePhraseResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RemovePhraseResponse;
  static deserializeBinaryFromReader(message: RemovePhraseResponse, reader: jspb.BinaryReader): RemovePhraseResponse;
}

export namespace RemovePhraseResponse {
  export type AsObject = {
    collection?: phrase_pb.CollectionView.AsObject,
  }
}

