import {Component, HostListener, OnInit} from '@angular/core';
import {PhraseTestComponent} from '../phrase-test/phrase-test.component';
import {SelectedOptions, SelectTranslationTest, TypeSourceTest, TypeTranslationTest} from '../../proto/exam_pb';
import {CommonModule} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";
import {MatRadioModule} from "@angular/material/radio";
import {FormsModule} from "@angular/forms";
import {ExamButtonComponent} from "../exam-button/exam-button.component";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-select-translation',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatRadioModule, FormsModule, ExamButtonComponent, MatIconModule],
  templateUrl: './select-translation.component.html',
  styleUrls: ['./select-translation.component.css']
})
export class SelectTranslationComponent extends PhraseTestComponent {

  getAnswerClass(option: SelectedOptions): string {
    if (this.checkAnswer) {
      if (this.isOptionCorrect(option)) {
        return 'correct-answer';
      }
      if (option.getTranslation() === this.answer && !this.isOptionCorrect(option)) {
        return 'wrong-answer';
      }
    }
    return '';
  }

  override isAnswerCorrect(): boolean {
    // @ts-ignore
    return this.getTranslationOptions().find(e => e.getTranslation() === this.answer).getIsCorrect();
  }

  getIsVisible(): boolean {
    return this.task instanceof SelectTranslationTest && !this.task.getIsPhraseHidden();
  }

  getText(): string {
    if (this.checkAnswer || this.getIsVisible()) {
      return this.getTest().getPhrase();
    }
    return '';
  }

  getTranslationOptions(): Array<SelectedOptions> {
    return this.getTest().getOptionsList();
  }

  getExample(): string {
    return this.getTest().getExample();
  }

  getPhraseClass(): string {
    if (this.getTest().getIsNewPhrase()) {
      return 'green';
    }
    return '';
  }

  isOptionCorrect(option: SelectedOptions): boolean {
    return option.getIsCorrect();
  }

  getTest(): SelectTranslationTest {
    if (this.task instanceof SelectTranslationTest) {
      return this.task;
    }
    throw '';
  }

  @HostListener('window:keyup', ['$event'])
  onKey(event: KeyboardEvent): void {
    const key = event.keyCode - 49;
    if (!this.checkAnswer && key >= 0 && key <= 5) {
      this.answer = this.getTranslationOptions()[key].getTranslation();
      this.onCheckClicked();
    }
  }

}
