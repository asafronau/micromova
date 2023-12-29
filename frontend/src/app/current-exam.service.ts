import {Injectable} from '@angular/core';
import {Exam, TaskResult} from '../proto/exam_pb';
import {BackendService} from './backend.service';
import {map, tap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {GenerateExamResponse} from '../proto/requests_pb';

@Injectable({
  providedIn: 'root'
})
export class CurrentExamService {

  collectionName: string = "";
  exam!: Exam;
  todayScore!: number;
  todayDiscovered!: number;
  weekDiscovered!: number;
  totalOpened!: number;
  totalUndiscovered!: number;
  starScore!: number;
  uniqueToday!: number;
  uniqueWeek!: number;
  taskResults: Array<TaskResult> = [];

  constructor(private backend: BackendService) { }

  generateExam(collectionName: string): Observable<void> {
    return this.backend.generateExam(collectionName, Intl.DateTimeFormat().resolvedOptions().timeZone).pipe(tap(response => {
      this.collectionName = collectionName;
      this.applyResponse(response);
    }), map(result => undefined));
  }

  applyResponse(response: GenerateExamResponse): void {
    this.exam = <Exam>response.getExam();
    this.todayScore = response.getTodayScore();
    this.todayDiscovered = response.getTodayDiscoveredPhrases();
    this.weekDiscovered = response.getWeekDiscoveredPhrases();
    this.starScore = response.getStarScoreMillis();
    this.totalOpened = response.getTotalOpenedPhrases();
    this.totalUndiscovered = response.getTotalUndiscoveredPhrases();
    this.uniqueToday = response.getUniquePhrasesToday();
    this.uniqueWeek = response.getUniquePhrasesWeek();
    this.taskResults = [];
  }

  getTotalResultCount(): number {
    return this.taskResults.length;
  }

  getCorrectResultCount(): number {
    return this.taskResults.filter(e => e.getIsCorrectAnswer()).length;
  }
}
