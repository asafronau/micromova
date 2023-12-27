import {SelectTranslationTest, SpellTest, TypeSourceTest, TypeTranslationTest} from '../proto/exam_pb';

export const enum TaskType {
  TASK_TYPE_UKNOWN,
  SELECT_TRANSLATION,
  SPELL,
  TYPE_TRANSLATION,
  TYPE_SOURCE,
}

type Task = SelectTranslationTest | SpellTest | TypeTranslationTest | TypeSourceTest;

export type { Task };
