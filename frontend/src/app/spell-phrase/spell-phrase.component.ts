import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {SelectTranslationTest, SpellTest} from '../../proto/exam_pb';
import {stringsEqual} from '../constants';
import {PhraseTestComponent} from '../phrase-test/phrase-test.component';
import {CommonModule} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {FormsModule} from "@angular/forms";
import {ExamButtonComponent} from "../exam-button/exam-button.component";

@Component({
  selector: 'app-spell-phrase',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatTooltipModule, MatButtonModule, MatInputModule, FormsModule, ExamButtonComponent],
  templateUrl: './spell-phrase.component.html',
  styleUrls: ['./spell-phrase.component.css']
})
export class SpellPhraseComponent extends PhraseTestComponent {

  @ViewChild('textInput', { read: ElementRef, static: true }) textInput!: ElementRef;

  override init(): void {
    setTimeout(() => this.textInput.nativeElement.focus(), 10);
  }

  getExample(): string {
    if (this.checkAnswer) {
      return this.getTest().getExample();
    }
    return '';
  }

  getCorrectAnswer(): string {
    return this.getTest().getPhrase();
  }

  getCorrectTranslation(): string {
    return this.getTest().getTranslation();
  }

  getAnswerClass(): string {
    if (!this.checkAnswer) {
      return '';
    }
    return this.isAnswerCorrect() ? 'correct-answer' : 'wrong-answer';
  }

  override isAnswerCorrect(): boolean {
    return stringsEqual(<string>this.answer, this.getCorrectAnswer());
  }

  verifyAnswer(): void {
    this.checkAnswer = true;
  }

  getTest(): SpellTest {
    if (this.task instanceof SpellTest) {
      return this.task;
    }
    throw '';
  }

}
