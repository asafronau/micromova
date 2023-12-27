import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";

@Component({
  selector: 'app-score-strength',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatTooltipModule],
  templateUrl: './score-strength.component.html',
  styleUrl: './score-strength.component.css'
})
export class ScoreStrengthComponent implements OnInit {

  @Input() starScoreMillis!: number;

  constructor() { }

  ngOnInit(): void {
  }

  getColor(): string {
    return `hsl(${120 * (this.starScoreMillis * this.starScoreMillis) / (3000 * 3000) }, 90%, 60%)`;
  }

  getScore(): string {
    if (this.starScoreMillis === 0) {
      return '0';
    }
    return Math.min(3, this.starScoreMillis / 1000).toFixed(2);
  }

  getEmptyStars(): Array<number> {
    return this.getNumberOfStars() === 0 ? Array(1) : Array(0);
  }

  getFullStars(): Array<number> {
    return Array(this.getNumberOfFullStars());
  }

  getHalfStars(): Array<number> {
    return Array(this.getNumberOfHalfStars());
  }

  private getNumberOfFullStars(): number {
    return Math.floor(this.getNumberOfStars());
  }

  private getNumberOfHalfStars(): number {
    return Math.ceil(this.getNumberOfStars() - this.getNumberOfFullStars());
  }

  private getNumberOfStars(): number {
    if (this.starScoreMillis === 0) {
      return 0;
    }
    return Math.round(Math.min(3, this.starScoreMillis / 1000) * 2) / 2;
  }
}
