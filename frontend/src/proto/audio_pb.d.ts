// package: micromova.proto
// file: audio.proto

import * as jspb from "google-protobuf";

export class Recording extends jspb.Message {
  getVoiceGender(): VoiceGenderMap[keyof VoiceGenderMap];
  setVoiceGender(value: VoiceGenderMap[keyof VoiceGenderMap]): void;

  getFormat(): FormatMap[keyof FormatMap];
  setFormat(value: FormatMap[keyof FormatMap]): void;

  getUri(): string;
  setUri(value: string): void;

  getType(): TypeMap[keyof TypeMap];
  setType(value: TypeMap[keyof TypeMap]): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Recording.AsObject;
  static toObject(includeInstance: boolean, msg: Recording): Recording.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Recording, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Recording;
  static deserializeBinaryFromReader(message: Recording, reader: jspb.BinaryReader): Recording;
}

export namespace Recording {
  export type AsObject = {
    voiceGender: VoiceGenderMap[keyof VoiceGenderMap],
    format: FormatMap[keyof FormatMap],
    uri: string,
    type: TypeMap[keyof TypeMap],
  }
}

export interface VoiceGenderMap {
  UNKNOWN_GENDER: 0;
  FEMALE: 1;
  MALE: 2;
}

export const VoiceGender: VoiceGenderMap;

export interface FormatMap {
  UNKNOWN_FORMAT: 0;
  MP3: 1;
}

export const Format: FormatMap;

export interface TypeMap {
  UNKNOWN_TYPE: 0;
  GOOGLE_TEXT_TO_SPEECH: 1;
}

export const Type: TypeMap;

