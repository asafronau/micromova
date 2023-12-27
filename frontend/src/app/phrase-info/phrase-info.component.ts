import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from '@angular/router';
import {PhraseHeadline} from '../../proto/phrase_pb';
import {CommonModule} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {ScoreStrengthComponent} from "../score-strength/score-strength.component";

@Component({
  selector: 'app-phrase-info',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, ScoreStrengthComponent],
  templateUrl: './phrase-info.component.html',
  styleUrls: ['./phrase-info.component.css']
})
export class PhraseInfoComponent  {

  @Input() phrase!: PhraseHeadline;
  @Input() listName!: string;
  @Output() phraseDeleted = new EventEmitter<void>();

  constructor(private router: Router) {}

  editPhrase(): void {
    this.router.navigateByUrl(
      `/phrase/${this.listName}/edit/${this.phrase.getId()}`);
  }

  deletePhrase(): void {
    this.phraseDeleted.emit();
  }

  getText(): string {
    // console.log(this.phrase);
    return this.phrase.getNormalizedText();
  }

  getStarScore(): number {
    return this.phrase.getStarScoreMillis();
  }

  isTodoPhrase(): boolean {
    return this.phrase.getIsDiscoverable();
  }
}
