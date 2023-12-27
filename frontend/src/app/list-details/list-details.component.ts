import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CollectionView, PhraseHeadline} from '../../proto/phrase_pb';
import {ActivatedRoute, Router} from '@angular/router';
import {BackendService} from '../backend.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatListModule} from "@angular/material/list";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {FormsModule} from "@angular/forms";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {PhraseInfoComponent} from "../phrase-info/phrase-info.component";

@Component({
  selector: 'app-list-details',
  standalone: true,
  imports: [CommonModule, MatListModule, MatCheckboxModule, FormsModule,
    MatTooltipModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, PhraseInfoComponent],
  templateUrl: './list-details.component.html',
  styleUrls: ['./list-details.component.css']
})
export class ListDetailsComponent {

  private view!: CollectionView;

  constructor(route: ActivatedRoute, private backend: BackendService, private router: Router, private snackBar: MatSnackBar) {
    route.params.subscribe(params => {
      backend.maybeLoadCollection(params['name']).subscribe(data => this.view = data);
    });
  }

  get discoverPoints(): number {
    return this.view.getDiscoverPoints();
  }

  set discoverPoints(value: number) {
    this.view.setDiscoverPoints(value);
  }

  get usePhraseDiscovery(): boolean {
    return this.view.getIsDiscoverEnabled();
  }

  set usePhraseDiscovery(value: boolean) {
    this.view.setIsDiscoverEnabled(value);
  }

  getCurrentPoints(): string {
    return 'Accrued points ' + this.view.getCurrentDiscoverPoints();
  }

  addPhrase(): Promise<boolean> {
    return this.router.navigateByUrl(`/phrase/${this.getCollectionName()}/add`);
  }

  deletePhrase(phrase: PhraseHeadline): void {
    this.backend.removePhrase(this.getCollectionName(), phrase.getId()).subscribe(response => this.view = <CollectionView>response.getCollection());
  }

  getCollectionName(): string {
    return this.view.getName();
  }

  getPhrases(): Array<PhraseHeadline> {
    return this.view.getPhraseList();
  }

  isComponentReady(): boolean {
    return !!this.view;
  }

  getDiscoverPoints(): number {
    return this.view.getDiscoverPoints();
  }

  saveDiscoverPoints(): void {
    this.backend.updateDiscover(
      this.view.getName(),
      this.view.getIsDiscoverEnabled(),
      this.view.getDiscoverPoints()
    ).subscribe(unused => this.snackBar.open('Saved',
      undefined, {duration: 1000}));
  }

}
