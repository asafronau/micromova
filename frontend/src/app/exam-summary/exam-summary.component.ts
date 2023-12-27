import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {CurrentExamService} from '../current-exam.service';
import {ActivatedRoute, Router} from '@angular/router';
import {BackendService} from '../backend.service';
import {MatButtonModule} from "@angular/material/button";
import {NgClass} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";

@Component({
  selector: 'app-exam-summary',
  standalone: true,
  templateUrl: './exam-summary.component.html',
  imports: [
    MatButtonModule,
    MatIconModule,
    NgClass
  ],
  styleUrls: ['./exam-summary.component.css']
})
export class ExamSummaryComponent implements OnInit {

  @ViewChild('replayBtn', { read: ElementRef, static: true }) replayBtn!: ElementRef;

  private phraseListName!: string;
  private isDone = false;
  private score!: string;
  private successRate!: number;

  constructor(route: ActivatedRoute, private examService: CurrentExamService, private router: Router, backend: BackendService) {
    route.params.subscribe(params => {
      this.phraseListName = params['name'];
      this.score = `${this.examService.getCorrectResultCount()} of ${this.examService.getTotalResultCount()}`;
      this.successRate = this.examService.getCorrectResultCount() / this.examService.getTotalResultCount();
      if (examService.getTotalResultCount() > 0) {
        backend.applyExam(this.phraseListName, Intl.DateTimeFormat().resolvedOptions().timeZone, examService.taskResults)
        .subscribe(data => {
          this.examService.applyResponse(data);
          setTimeout(() => this.replayBtn.nativeElement.focus(), 30);
          this.isDone = true;
        });
      }
    });
  }

  ngOnInit(): void {
  }

  getScore(): string {
    return this.score;
  }

  private getSuccessRate(): number {
    return this.successRate;
  }

  isPerfectResult(): boolean {
    return this.getSuccessRate() >= 0.999;
  }

  isGoodResult(): boolean {
    return this.getSuccessRate() > 0.91;
  }

  isRetryDisabled(): boolean {
    return !this.isDone;
  }

  retryExam(): void {
    this.router.navigateByUrl(`/exam/${this.phraseListName}`);
  }
}
