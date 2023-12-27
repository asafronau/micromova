import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {async, Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {stringsEqual} from '../constants';
import {PhraseTestComponent} from '../phrase-test/phrase-test.component';
import {SelectTranslationTest, TypeSourceTest, TypeTranslationTest} from '../../proto/exam_pb';
import {MatButtonModule} from "@angular/material/button";
import {MatInputModule} from "@angular/material/input";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {ExamButtonComponent} from "../exam-button/exam-button.component";
import {CommonModule, NgClass} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-select-answer',
  standalone: true,
  templateUrl: './select-answer.component.html',
  imports: [
    CommonModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    ExamButtonComponent,
    NgClass
  ],
  styleUrls: ['./select-answer.component.css']
})
export class SelectAnswerComponent extends PhraseTestComponent implements OnInit {

  @ViewChild('textInput', { read: ElementRef, static: true }) textInput!: ElementRef;

  myControl = new FormControl();
  filteredOptions: Observable<string[]>|undefined;

  @Input() translatable!: string;
  @Input() allOptions!: Array<string>;
  @Input() correctOptions!: Array<string>;

  override ngOnInit(): void {
    this.filteredOptions = this.myControl.valueChanges
    .pipe(
      startWith(''),
      map(value => this._filter(value))
    );
  }

  override init(): void {
    super.init();
    setTimeout(() => this.textInput.nativeElement.focus(), 10);
    this.filteredOptions = this.myControl.valueChanges
    .pipe(
      startWith(''),
      map(value => this._filter(value))
    );
  }

  // @ts-ignore
  get answer(): string {
    return this.myControl.value;
  }

  override set answer(value) {
    this.myControl.setValue(value);
  }

  override onCheckClicked(): void {
    if (!this.isCheckAnswerDisabled()) {
      setTimeout(() => {
        super.onCheckClicked();
        this.myControl.disable();
        this.filteredOptions = undefined;
      }, 50);
    }
  }

  override onContinueClicked(): void {
    super.onContinueClicked();
    this.myControl.enable();
  }

  getExample(): string {
    if (this.checkAnswer) {
      return this.getTest().getExample();
    }
    return '';
  }

  getTest(): TypeTranslationTest | TypeSourceTest {
    if (this.task instanceof TypeTranslationTest || this.task instanceof TypeSourceTest) {
      return this.task;
    }
    throw '';
  }

  private _filter(value: string): string[] {
    const filterValue = value ? value.toLowerCase() : '';

    return this.allOptions.filter(
      option => option.toLowerCase().includes(filterValue));
  }

  getAnswerClass(): string {
    if (!this.checkAnswer) {
      return '';
    }
    return this.isAnswerCorrect() ? 'correct-answer' : 'wrong-answer';
  }

  getIsVisible(): boolean {
    return this.task instanceof TypeSourceTest || (this.task instanceof TypeTranslationTest && !this.task.getIsPhraseHidden());
  }

  getIsTypeSource(): boolean {
    return this.task instanceof TypeSourceTest;
  }

  getText(): string {
    if (this.checkAnswer || this.getIsVisible()) {
      return this.translatable;
    }
    return '';
  }

  override isAnswerCorrect(): boolean {
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < this.correctOptions.length; i++) {
      if (stringsEqual(this.answer, this.correctOptions[i])) {
        return true;
      }
    }
    return false;
  }

  getCorrectAnswer(): string {
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < this.correctOptions.length; i++) {
      if (!stringsEqual(this.answer, this.correctOptions[i])) {
        return this.correctOptions[i];
      }
    }
    return this.correctOptions[0];
  }
}
