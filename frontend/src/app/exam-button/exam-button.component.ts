import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {ExamButtonRole} from '../constants';
import {MatButtonModule} from "@angular/material/button";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-exam-button',
  standalone: true,
  templateUrl: './exam-button.component.html',
  imports: [
    CommonModule,
    MatButtonModule,
  ],
  styleUrls: ['./exam-button.component.css']
})
export class ExamButtonComponent  {
  private buttonRole!: ExamButtonRole;

  @ViewChild('nextButton', { read: ElementRef }) nextButton!: ElementRef;

  @Input() checkAnswerDisabled!: boolean;

  @Input()
  set role(value: ExamButtonRole) {
    this.buttonRole = value;
    if (!this.isCheckAnswer()) {
      setTimeout(() => this.nextButton.nativeElement.focus(), 50);
    }
  }
  get role(): ExamButtonRole {
    return this.buttonRole;
  }

  @Output() checkClicked = new EventEmitter<void>();
  @Output() continueClicked = new EventEmitter<void>();

  constructor() { }


  checkAnswer(): void {
    this.checkClicked.emit();
    setTimeout(() => this.nextButton.nativeElement.focus(), 50);
  }

  continueExam(): void {
    this.continueClicked.emit();
  }

  isCheckAnswer(): boolean {
    return this.role === ExamButtonRole.CHECK;
  }

  isCheckAnswerDisabled(): boolean {
    return this.checkAnswerDisabled;
  }
}
