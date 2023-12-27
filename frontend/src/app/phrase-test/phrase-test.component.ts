import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SelectTranslationTest, SpellTest, TaskResult, TypeSourceTest, TypeTranslationTest} from '../../proto/exam_pb';
import {Task} from '../tasktype';
import {cleanString, ExamButtonRole} from '../constants';
import {CurrentExamService} from '../current-exam.service';

@Component({
  standalone: true,
  template: '',
})
export class PhraseTestComponent implements OnInit {

  public phraseTask!: Task;

  @Input() set task(value: Task) {
    this.answer = undefined;
    this.checkAnswer = false;

    setTimeout(() => this.playSound(), 250);
    this.phraseTask = value;

    this.init();
  }

  @Input() cachedAudio!: HTMLAudioElement;

  get task(): Task {
    return this.phraseTask;
  }

  @Output() continueClicked = new EventEmitter<void>();

  answer: string|undefined;
  checkAnswer = false;

  constructor(private currentExamService: CurrentExamService) {
  }

  init(): void {}

  ngOnInit(): void {}

  getButtonRole(): ExamButtonRole {
    if (this.checkAnswer) {
      return ExamButtonRole.CONTINUE;
    } else {
      return ExamButtonRole.CHECK;
    }
  }

  isAnswerCorrect(): boolean {
    throw new Error('non-overriden method');
  }

  isCheckAnswerDisabled(): boolean {
    return this.answer === undefined || !cleanString(this.answer);
  }

  onContinueClicked(): void {
    this.continueClicked.emit();
  }

  onCheckClicked(): void {
    if (this.isCheckAnswerDisabled()) {
      return;
    }
    this.checkAnswer = true;
    const result = new TaskResult();
    result.setId(this.phraseTask.getId());
    result.setIsCorrectAnswer(this.isAnswerCorrect());
    if (this.isAnswerCorrect()) {
      result.setPhraseScore(this.phraseTask.getCorrectScorePhrase());
      result.setUserScore(this.phraseTask.getCorrectScoreUser());
    } else {
      result.setPhraseScore(this.phraseTask.getWrongScorePhrase());
      result.setUserScore(this.phraseTask.getWrongScoreUser());
    }
    this.currentExamService.todayScore += result.getUserScore();
    this.currentExamService.taskResults.push(result);
  }

  playSound(): void {
    if (!this.phraseTask.getMp3Url()) {
      return;
    }
    this.cachedAudio.play();
  }
}
