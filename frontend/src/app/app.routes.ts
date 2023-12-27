import { Routes } from '@angular/router';
import {CreateListComponent} from "./create-list/create-list.component";
import {ListsComponent} from "./lists/lists.component";
import {ListDetailsComponent} from "./list-details/list-details.component";
import {PhraseEditComponent} from "./phrase-edit/phrase-edit.component";
import {ExamSummaryComponent} from "./exam-summary/exam-summary.component";
import {ExamComponent} from "./exam/exam.component";

export const routes: Routes = [
  {path: 'lists', component: ListsComponent},
  {path: 'list/:name', component: ListDetailsComponent},
  {path: 'phrase/:name/add', component: PhraseEditComponent, data: {isAdd: true}},
  {path: 'phrase/:name/edit/:id', component: PhraseEditComponent, data: {isAdd: false}},
  {path: 'lists/add', component: CreateListComponent},
  {path: 'exam/:name', component: ExamComponent},
  {path: 'exam/:name/summary', component: ExamSummaryComponent},
];
