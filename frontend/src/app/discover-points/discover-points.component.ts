import { Component, OnInit } from '@angular/core';
import {CurrentExamService} from '../current-exam.service';
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-discover-points',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './discover-points.component.html',
  styleUrls: ['./discover-points.component.css']
})
export class DiscoverPointsComponent implements OnInit {

  constructor(private examService: CurrentExamService) { }

  ngOnInit(): void {
  }

  getWordsToday(): number {
    return this.examService.todayDiscovered;
  }

  getWordsWeek(): number {
    return this.examService.weekDiscovered;
  }
}
