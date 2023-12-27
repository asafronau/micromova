import { Injectable } from '@angular/core';
import {CollectionView} from '../proto/phrase_pb';
import {BackendService} from './backend.service';

@Injectable({
  providedIn: 'root'
})
export class CurrentCollectionService {

  // TODO: pre-parse, select phrases.
  collection!: CollectionView;

  get(): CollectionView {
    return this.collection;
  }

  isLoaded(name: string): boolean {
    return this.collection != null && this.collection.getName() === name;
  }

  update(collection: CollectionView): void {
    this.collection = collection;
  }
}
