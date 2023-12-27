import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BackendService} from '../backend.service';
import {Exam, SelectTranslationTest, SpellTest, TypeSourceTest, TypeTranslationTest} from '../../proto/exam_pb';
import {Task, TaskType} from '../tasktype';
import {CurrentExamService} from '../current-exam.service';
import {CommonModule} from "@angular/common";
import {ScoreStrengthComponent} from "../score-strength/score-strength.component";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatTooltipModule} from "@angular/material/tooltip";
import {SelectTranslationComponent} from "../select-translation/select-translation.component";
import {SpellPhraseComponent} from "../spell-phrase/spell-phrase.component";
import {SelectAnswerComponent} from "../select-answer/select-answer.component";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {DiscoverPointsComponent} from "../discover-points/discover-points.component";

@Component({
  selector: 'app-exam',
  standalone: true,
  imports: [CommonModule, ScoreStrengthComponent, MatProgressBarModule, MatTooltipModule, SelectTranslationComponent, SpellPhraseComponent, SelectAnswerComponent, MatProgressSpinnerModule, DiscoverPointsComponent],
  templateUrl: './exam.component.html',
  styleUrls: ['./exam.component.css']
})
export class ExamComponent  {

  exam!: Exam;
  todayDiscovered!: number;
  weekDiscovered!: number;
  starScore!: number;
  totalOpened!: number;
  totalUndiscovered!: number;
  taskIndex = 0;
  collectionName!: string;
  uniqueToday!: number;
  uniqueWeek!: number;
  tasks: Array<Task> = [];
  audioEls: Array<HTMLAudioElement> = [];

  constructor(route: ActivatedRoute, private router: Router, private backend: BackendService, private examService: CurrentExamService) {
    route.params.subscribe(params => {
      this.collectionName = params['name'];
      if (this.examService.exam == null || this.examService.taskResults.length !== 0) {
        examService.generateExam(this.collectionName).subscribe(unused => {
          this.copyExam();
        });
      } else {
        this.copyExam();
      }
    });
  }

  private copyExam(): void {
    this.tasks = [];
    this.audioEls = [];
    this.exam = this.examService.exam;
    this.todayDiscovered = this.examService.todayDiscovered;
    this.weekDiscovered = this.examService.weekDiscovered;
    this.starScore = this.examService.starScore;
    this.totalOpened = this.examService.totalOpened;
    this.totalUndiscovered = this.examService.totalUndiscovered;
    this.uniqueToday = this.examService.uniqueToday;
    this.uniqueWeek = this.examService.uniqueWeek;
    for (let i = 0; i < this.exam.getOrderList().length; i++) {
      this.tasks.push(this.getTaskByIndex(i));
      this.audioEls.push(this.makeAudioEl(i));
    }
  }

  // @ts-ignore
  getAudio(): HTMLAudioElement {
    if (!!this.exam) {
      return this.audioEls[this.taskIndex];
    }
  }

  // @ts-ignore
  makeAudioEl(idx: number): HTMLAudioElement {
    const suffix = this.tasks[idx].getMp3Url();
    if (!!suffix) {
      const path = 'https://storage.googleapis.com/' + suffix;
      const audioEl = new Audio(path);
      audioEl.preload = 'auto';
      return audioEl;
    }
  }

  isComponentReady(): boolean {
    return this.exam != null;
  }

  getProgress(): number {
    return 100 * this.taskIndex / this.getTaskCount();
  }

  getScore(): number {
    return this.examService.todayScore;
  }

  getUnique(): number {
    return this.examService.uniqueToday;
  }

  // @ts-ignore
  getCorrectTranslations(): Array<string> {
    const task = this.getTask();
    if (task instanceof  TypeTranslationTest) {
      return task.getCorrectTranslationsList();
    }
  }


  // @ts-ignore
  getTranslatable(): string {
    const task = this.getTask();
    if (task instanceof TypeTranslationTest) {
      return task.getPhrase();
    } else if (task instanceof TypeSourceTest) {
      return task.getTranslation();
    }
  }

  // @ts-ignore
  getCorrectSources(): Array<string> {
    const task = this.getTask();
    if (task instanceof  TypeSourceTest) {
      return task.getCorrectSourcesList();
    }
  }

  getAllTranslations(): Array<string> {
    return this.exam.getTranslationPhrasesList();
  }

  getAllSources(): Array<string> {
    return this.exam.getSourcePhrasesList();
  }

  // @ts-ignore
  getTaskType(): TaskType {
    if (!!this.exam) {
      const task = this.getTask();
      if (task instanceof SelectTranslationTest) {
        return TaskType.SELECT_TRANSLATION;
      } else if (task instanceof SpellTest) {
        return TaskType.SPELL;
      } else if (task instanceof  TypeTranslationTest) {
        return TaskType.TYPE_TRANSLATION;
      } else if (task instanceof TypeSourceTest) {
        return TaskType.TYPE_SOURCE;
      }
      // @ts-ignore
      return undefined;
    }
  }

  getSelectTranslationType(): TaskType {
    return TaskType.SELECT_TRANSLATION;
  }

  getSpellType(): TaskType {
    return TaskType.SPELL;
  }

  getTypeTranslationType(): TaskType {
    return TaskType.TYPE_TRANSLATION;
  }

  getTypeSourceType(): TaskType {
    return TaskType.TYPE_SOURCE;
  }

  getTaskByIndex(idx: number): Task {
    if (!!this.exam) {
      idx = this.exam.getOrderList()[idx];
      const tasks: Array<any> = [
        this.exam.getSelectTranslationTestsList(),
        this.exam.getSpellTestsList(),
        this.exam.getTypeTranslationTestsList(),
        this.exam.getTypeSourceTestsList()];
      let i = 0;
      for (const group of tasks) {
        if (i + group.length > idx) {
          return group[idx - i];
        } else {
          i += group.length;
        }
      }
    }
    // @ts-ignore
    return undefined;
  }

  // @ts-ignore
  getTask(): Task {
    if (!!this.exam) {
      return this.tasks[this.taskIndex];
    }
  }

  getTaskCount(): number {
    return this.exam.getSelectTranslationTestsList().length
      + this.exam.getSpellTestsList().length
      + this.exam.getTypeTranslationTestsList().length
      + this.exam.getTypeSourceTestsList().length;
  }

  getRecentScore(): number {
    return this.starScore;
  }

  getTotalCount(): number {
    return this.totalOpened;
  }

  getTODO(): number {
    return this.totalUndiscovered;
  }

  getUniqueStats(): string {
    return 'Studied ' + this.uniqueToday + ' phrases today, ' + this.uniqueWeek + ' this week';
  }

  onContinueClicked(): void {
    //  this.router.navigate([..], { relativeTo: this.route })
    if (this.taskIndex === (this.getTaskCount() - 1)) {
      this.router.navigateByUrl(`/exam/${this.collectionName}/summary`);
    }
    this.taskIndex++;
  }
}
