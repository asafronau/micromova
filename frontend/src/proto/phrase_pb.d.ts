// package: micromova.proto
// file: phrase.proto

import * as jspb from "google-protobuf";
import * as audio_pb from "./audio_pb";
import * as language_pb from "./language_pb";

export class Translation extends jspb.Message {
  getLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  getText(): string;
  setText(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Translation.AsObject;
  static toObject(includeInstance: boolean, msg: Translation): Translation.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Translation, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Translation;
  static deserializeBinaryFromReader(message: Translation, reader: jspb.BinaryReader): Translation;
}

export namespace Translation {
  export type AsObject = {
    language: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    text: string,
  }
}

export class Phrase extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getNormalizedText(): string;
  setNormalizedText(value: string): void;

  getSourceLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setSourceLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  clearTranslationList(): void;
  getTranslationList(): Array<Translation>;
  setTranslationList(value: Array<Translation>): void;
  addTranslation(value?: Translation, index?: number): Translation;

  clearRecordingList(): void;
  getRecordingList(): Array<audio_pb.Recording>;
  setRecordingList(value: Array<audio_pb.Recording>): void;
  addRecording(value?: audio_pb.Recording, index?: number): audio_pb.Recording;

  getStarScoreMillis(): number;
  setStarScoreMillis(value: number): void;

  getSuccessTimeSeconds(): number;
  setSuccessTimeSeconds(value: number): void;

  getScore(): number;
  setScore(value: number): void;

  getIsDiscoverable(): boolean;
  setIsDiscoverable(value: boolean): void;

  getCorrectAnswersDelta(): number;
  setCorrectAnswersDelta(value: number): void;

  getExample(): string;
  setExample(value: string): void;

  getComment(): string;
  setComment(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Phrase.AsObject;
  static toObject(includeInstance: boolean, msg: Phrase): Phrase.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Phrase, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Phrase;
  static deserializeBinaryFromReader(message: Phrase, reader: jspb.BinaryReader): Phrase;
}

export namespace Phrase {
  export type AsObject = {
    id: string,
    normalizedText: string,
    sourceLanguage: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    translationList: Array<Translation.AsObject>,
    recordingList: Array<audio_pb.Recording.AsObject>,
    starScoreMillis: number,
    successTimeSeconds: number,
    score: number,
    isDiscoverable: boolean,
    correctAnswersDelta: number,
    example: string,
    comment: string,
  }
}

export class RecentScores extends jspb.Message {
  getTimestamp(): number;
  setTimestamp(value: number): void;

  getScore(): number;
  setScore(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecentScores.AsObject;
  static toObject(includeInstance: boolean, msg: RecentScores): RecentScores.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecentScores, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecentScores;
  static deserializeBinaryFromReader(message: RecentScores, reader: jspb.BinaryReader): RecentScores;
}

export namespace RecentScores {
  export type AsObject = {
    timestamp: number,
    score: number,
  }
}

export class Collection extends jspb.Message {
  getName(): string;
  setName(value: string): void;

  getUserId(): string;
  setUserId(value: string): void;

  getSourceLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setSourceLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  getTranslationLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setTranslationLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  getDiscoverPoints(): number;
  setDiscoverPoints(value: number): void;

  getCurrentDiscoverPoints(): number;
  setCurrentDiscoverPoints(value: number): void;

  clearPhraseList(): void;
  getPhraseList(): Array<Phrase>;
  setPhraseList(value: Array<Phrase>): void;
  addPhrase(value?: Phrase, index?: number): Phrase;

  getIsDiscoverEnabled(): boolean;
  setIsDiscoverEnabled(value: boolean): void;

  clearRecentDiscoverPhraseEventTimeList(): void;
  getRecentDiscoverPhraseEventTimeList(): Array<number>;
  setRecentDiscoverPhraseEventTimeList(value: Array<number>): void;
  addRecentDiscoverPhraseEventTime(value: number, index?: number): number;

  clearRecentScoreEventTimeList(): void;
  getRecentScoreEventTimeList(): Array<RecentScores>;
  setRecentScoreEventTimeList(value: Array<RecentScores>): void;
  addRecentScoreEventTime(value?: RecentScores, index?: number): RecentScores;

  getVersion(): number;
  setVersion(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Collection.AsObject;
  static toObject(includeInstance: boolean, msg: Collection): Collection.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Collection, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Collection;
  static deserializeBinaryFromReader(message: Collection, reader: jspb.BinaryReader): Collection;
}

export namespace Collection {
  export type AsObject = {
    name: string,
    userId: string,
    sourceLanguage: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    translationLanguage: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    discoverPoints: number,
    currentDiscoverPoints: number,
    phraseList: Array<Phrase.AsObject>,
    isDiscoverEnabled: boolean,
    recentDiscoverPhraseEventTimeList: Array<number>,
    recentScoreEventTimeList: Array<RecentScores.AsObject>,
    version: number,
  }
}

export class PhraseHeadline extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getNormalizedText(): string;
  setNormalizedText(value: string): void;

  getStarScoreMillis(): number;
  setStarScoreMillis(value: number): void;

  getIsDiscoverable(): boolean;
  setIsDiscoverable(value: boolean): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): PhraseHeadline.AsObject;
  static toObject(includeInstance: boolean, msg: PhraseHeadline): PhraseHeadline.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: PhraseHeadline, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): PhraseHeadline;
  static deserializeBinaryFromReader(message: PhraseHeadline, reader: jspb.BinaryReader): PhraseHeadline;
}

export namespace PhraseHeadline {
  export type AsObject = {
    id: string,
    normalizedText: string,
    starScoreMillis: number,
    isDiscoverable: boolean,
  }
}

export class CollectionView extends jspb.Message {
  getName(): string;
  setName(value: string): void;

  getUserId(): string;
  setUserId(value: string): void;

  getSourceLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setSourceLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  getTranslationLanguage(): language_pb.LanguageMap[keyof language_pb.LanguageMap];
  setTranslationLanguage(value: language_pb.LanguageMap[keyof language_pb.LanguageMap]): void;

  getDiscoverPoints(): number;
  setDiscoverPoints(value: number): void;

  getCurrentDiscoverPoints(): number;
  setCurrentDiscoverPoints(value: number): void;

  getIsDiscoverEnabled(): boolean;
  setIsDiscoverEnabled(value: boolean): void;

  clearPhraseList(): void;
  getPhraseList(): Array<PhraseHeadline>;
  setPhraseList(value: Array<PhraseHeadline>): void;
  addPhrase(value?: PhraseHeadline, index?: number): PhraseHeadline;

  getVersion(): number;
  setVersion(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CollectionView.AsObject;
  static toObject(includeInstance: boolean, msg: CollectionView): CollectionView.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CollectionView, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CollectionView;
  static deserializeBinaryFromReader(message: CollectionView, reader: jspb.BinaryReader): CollectionView;
}

export namespace CollectionView {
  export type AsObject = {
    name: string,
    userId: string,
    sourceLanguage: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    translationLanguage: language_pb.LanguageMap[keyof language_pb.LanguageMap],
    discoverPoints: number,
    currentDiscoverPoints: number,
    isDiscoverEnabled: boolean,
    phraseList: Array<PhraseHeadline.AsObject>,
    version: number,
  }
}

