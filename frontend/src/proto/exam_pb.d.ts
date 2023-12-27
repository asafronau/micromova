// package: micromova.proto
// file: exam.proto

import * as jspb from "google-protobuf";

export class SelectedOptions extends jspb.Message {
  getTranslation(): string;
  setTranslation(value: string): void;

  getIsCorrect(): boolean;
  setIsCorrect(value: boolean): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SelectedOptions.AsObject;
  static toObject(includeInstance: boolean, msg: SelectedOptions): SelectedOptions.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: SelectedOptions, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SelectedOptions;
  static deserializeBinaryFromReader(message: SelectedOptions, reader: jspb.BinaryReader): SelectedOptions;
}

export namespace SelectedOptions {
  export type AsObject = {
    translation: string,
    isCorrect: boolean,
  }
}

export class SelectTranslationTest extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getPhrase(): string;
  setPhrase(value: string): void;

  getIsPhraseHidden(): boolean;
  setIsPhraseHidden(value: boolean): void;

  clearOptionsList(): void;
  getOptionsList(): Array<SelectedOptions>;
  setOptionsList(value: Array<SelectedOptions>): void;
  addOptions(value?: SelectedOptions, index?: number): SelectedOptions;

  getCorrectScoreUser(): number;
  setCorrectScoreUser(value: number): void;

  getWrongScoreUser(): number;
  setWrongScoreUser(value: number): void;

  getCorrectScorePhrase(): number;
  setCorrectScorePhrase(value: number): void;

  getWrongScorePhrase(): number;
  setWrongScorePhrase(value: number): void;

  getIsNewPhrase(): boolean;
  setIsNewPhrase(value: boolean): void;

  getExample(): string;
  setExample(value: string): void;

  getMp3Url(): string;
  setMp3Url(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SelectTranslationTest.AsObject;
  static toObject(includeInstance: boolean, msg: SelectTranslationTest): SelectTranslationTest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: SelectTranslationTest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SelectTranslationTest;
  static deserializeBinaryFromReader(message: SelectTranslationTest, reader: jspb.BinaryReader): SelectTranslationTest;
}

export namespace SelectTranslationTest {
  export type AsObject = {
    id: string,
    phrase: string,
    isPhraseHidden: boolean,
    optionsList: Array<SelectedOptions.AsObject>,
    correctScoreUser: number,
    wrongScoreUser: number,
    correctScorePhrase: number,
    wrongScorePhrase: number,
    isNewPhrase: boolean,
    example: string,
    mp3Url: string,
  }
}

export class SpellTest extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getPhrase(): string;
  setPhrase(value: string): void;

  getCorrectScoreUser(): number;
  setCorrectScoreUser(value: number): void;

  getWrongScoreUser(): number;
  setWrongScoreUser(value: number): void;

  getCorrectScorePhrase(): number;
  setCorrectScorePhrase(value: number): void;

  getWrongScorePhrase(): number;
  setWrongScorePhrase(value: number): void;

  getExample(): string;
  setExample(value: string): void;

  getTranslation(): string;
  setTranslation(value: string): void;

  getMp3Url(): string;
  setMp3Url(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): SpellTest.AsObject;
  static toObject(includeInstance: boolean, msg: SpellTest): SpellTest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: SpellTest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): SpellTest;
  static deserializeBinaryFromReader(message: SpellTest, reader: jspb.BinaryReader): SpellTest;
}

export namespace SpellTest {
  export type AsObject = {
    id: string,
    phrase: string,
    correctScoreUser: number,
    wrongScoreUser: number,
    correctScorePhrase: number,
    wrongScorePhrase: number,
    example: string,
    translation: string,
    mp3Url: string,
  }
}

export class TypeTranslationTest extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getPhrase(): string;
  setPhrase(value: string): void;

  clearCorrectTranslationsList(): void;
  getCorrectTranslationsList(): Array<string>;
  setCorrectTranslationsList(value: Array<string>): void;
  addCorrectTranslations(value: string, index?: number): string;

  getIsPhraseHidden(): boolean;
  setIsPhraseHidden(value: boolean): void;

  getCorrectScoreUser(): number;
  setCorrectScoreUser(value: number): void;

  getWrongScoreUser(): number;
  setWrongScoreUser(value: number): void;

  getCorrectScorePhrase(): number;
  setCorrectScorePhrase(value: number): void;

  getWrongScorePhrase(): number;
  setWrongScorePhrase(value: number): void;

  getExample(): string;
  setExample(value: string): void;

  getMp3Url(): string;
  setMp3Url(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TypeTranslationTest.AsObject;
  static toObject(includeInstance: boolean, msg: TypeTranslationTest): TypeTranslationTest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: TypeTranslationTest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TypeTranslationTest;
  static deserializeBinaryFromReader(message: TypeTranslationTest, reader: jspb.BinaryReader): TypeTranslationTest;
}

export namespace TypeTranslationTest {
  export type AsObject = {
    id: string,
    phrase: string,
    correctTranslationsList: Array<string>,
    isPhraseHidden: boolean,
    correctScoreUser: number,
    wrongScoreUser: number,
    correctScorePhrase: number,
    wrongScorePhrase: number,
    example: string,
    mp3Url: string,
  }
}

export class TypeSourceTest extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getTranslation(): string;
  setTranslation(value: string): void;

  clearCorrectSourcesList(): void;
  getCorrectSourcesList(): Array<string>;
  setCorrectSourcesList(value: Array<string>): void;
  addCorrectSources(value: string, index?: number): string;

  getCorrectScoreUser(): number;
  setCorrectScoreUser(value: number): void;

  getWrongScoreUser(): number;
  setWrongScoreUser(value: number): void;

  getCorrectScorePhrase(): number;
  setCorrectScorePhrase(value: number): void;

  getWrongScorePhrase(): number;
  setWrongScorePhrase(value: number): void;

  getExample(): string;
  setExample(value: string): void;

  getMp3Url(): string;
  setMp3Url(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TypeSourceTest.AsObject;
  static toObject(includeInstance: boolean, msg: TypeSourceTest): TypeSourceTest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: TypeSourceTest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TypeSourceTest;
  static deserializeBinaryFromReader(message: TypeSourceTest, reader: jspb.BinaryReader): TypeSourceTest;
}

export namespace TypeSourceTest {
  export type AsObject = {
    id: string,
    translation: string,
    correctSourcesList: Array<string>,
    correctScoreUser: number,
    wrongScoreUser: number,
    correctScorePhrase: number,
    wrongScorePhrase: number,
    example: string,
    mp3Url: string,
  }
}

export class Exam extends jspb.Message {
  clearSelectTranslationTestsList(): void;
  getSelectTranslationTestsList(): Array<SelectTranslationTest>;
  setSelectTranslationTestsList(value: Array<SelectTranslationTest>): void;
  addSelectTranslationTests(value?: SelectTranslationTest, index?: number): SelectTranslationTest;

  clearSpellTestsList(): void;
  getSpellTestsList(): Array<SpellTest>;
  setSpellTestsList(value: Array<SpellTest>): void;
  addSpellTests(value?: SpellTest, index?: number): SpellTest;

  clearTypeTranslationTestsList(): void;
  getTypeTranslationTestsList(): Array<TypeTranslationTest>;
  setTypeTranslationTestsList(value: Array<TypeTranslationTest>): void;
  addTypeTranslationTests(value?: TypeTranslationTest, index?: number): TypeTranslationTest;

  clearTypeSourceTestsList(): void;
  getTypeSourceTestsList(): Array<TypeSourceTest>;
  setTypeSourceTestsList(value: Array<TypeSourceTest>): void;
  addTypeSourceTests(value?: TypeSourceTest, index?: number): TypeSourceTest;

  clearSourcePhrasesList(): void;
  getSourcePhrasesList(): Array<string>;
  setSourcePhrasesList(value: Array<string>): void;
  addSourcePhrases(value: string, index?: number): string;

  clearTranslationPhrasesList(): void;
  getTranslationPhrasesList(): Array<string>;
  setTranslationPhrasesList(value: Array<string>): void;
  addTranslationPhrases(value: string, index?: number): string;

  clearOrderList(): void;
  getOrderList(): Array<number>;
  setOrderList(value: Array<number>): void;
  addOrder(value: number, index?: number): number;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Exam.AsObject;
  static toObject(includeInstance: boolean, msg: Exam): Exam.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Exam, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Exam;
  static deserializeBinaryFromReader(message: Exam, reader: jspb.BinaryReader): Exam;
}

export namespace Exam {
  export type AsObject = {
    selectTranslationTestsList: Array<SelectTranslationTest.AsObject>,
    spellTestsList: Array<SpellTest.AsObject>,
    typeTranslationTestsList: Array<TypeTranslationTest.AsObject>,
    typeSourceTestsList: Array<TypeSourceTest.AsObject>,
    sourcePhrasesList: Array<string>,
    translationPhrasesList: Array<string>,
    orderList: Array<number>,
  }
}

export class TaskResult extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getUserScore(): number;
  setUserScore(value: number): void;

  getPhraseScore(): number;
  setPhraseScore(value: number): void;

  getIsCorrectAnswer(): boolean;
  setIsCorrectAnswer(value: boolean): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TaskResult.AsObject;
  static toObject(includeInstance: boolean, msg: TaskResult): TaskResult.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: TaskResult, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TaskResult;
  static deserializeBinaryFromReader(message: TaskResult, reader: jspb.BinaryReader): TaskResult;
}

export namespace TaskResult {
  export type AsObject = {
    id: string,
    userScore: number,
    phraseScore: number,
    isCorrectAnswer: boolean,
  }
}

export interface TestTypeMap {
  UNKNOWN_TEST_TYPE: 0;
  SELECT_TRANSLATION_SHOW_PHRASE: 1;
  SELECT_TRANSLATION_HIDE_PHRASE: 2;
  SPELL: 3;
  TYPE_TRANSLATION_SHOW_PHRASE: 4;
  TYPE_TRANSLATION_HIDE_PHRASE: 5;
  TYPE_SOURCE: 6;
}

export const TestType: TestTypeMap;

