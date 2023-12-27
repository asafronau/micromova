import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatChipInputEvent, MatChipsModule} from '@angular/material/chips';
import {ENTER} from '@angular/cdk/keycodes';
import {Language} from '../../proto/language_pb';
import {ActivatedRoute, Router} from '@angular/router';
import {BackendService} from '../backend.service';
import {Phrase, Translation} from '../../proto/phrase_pb';
import {CurrentCollectionService} from '../current-collection.service';
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-phrase-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule, MatInputModule, MatCheckboxModule, MatButtonModule, MatChipsModule],
  templateUrl: './phrase-edit.component.html',
  styleUrls: ['./phrase-edit.component.css']
})
export class PhraseEditComponent  {

  constructor(private route: ActivatedRoute, private router: Router,
              private backend: BackendService, private collectionService: CurrentCollectionService) {
    route.params.subscribe(params => {
      this.collectionName = params['name'];
      const id = params['id'];
      this.isAdd = this.route.snapshot.data['isAdd'];
      if (!this.isAdd) {
        backend.loadPhrase(id, this.collectionName).subscribe(data => {
          this.phrase = <Phrase>data.getPhrase();
        });
      } else {
        backend.maybeLoadCollection(this.collectionName).subscribe(unused => {
          this.phrase = new Phrase();
          this.phrase.setSourceLanguage(this.collectionService.get().getSourceLanguage());
          this.phrase.setIsDiscoverable(true);
        });
      }
    });
  }
  @ViewChild('examInput', { read: ElementRef }) examInput!: ElementRef;

  collectionName!: string;
  phrase!: Phrase;

  isAdd = false;

  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;

  readonly separatorKeysCodes: number[] = [ENTER];

  isComponentLoaded(): boolean {
    return !!this.phrase;
  }

  getPhrasePlaceholder(): string {
    return `Enter a phrase`;
  }

  getListLang(): string {
    return 'Deutsch';
  }

  getTranslations(): Array<Translation> {
    return this.phrase.getTranslationList();
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    if ((value || '').trim()) {
      const translation = new Translation();
      translation.setText(value.trim());
      translation.setLanguage(Language.RU);
      this.phrase.addTranslation(translation);
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  get phraseText(): string {
    return this.phrase.getNormalizedText();
  }

  set phraseText(value: string) {
    this.phrase.setNormalizedText(value);
  }

  get isDiscoverable(): boolean {
    return this.phrase.getIsDiscoverable();
  }

  set isDiscoverable(value: boolean) {
    this.phrase.setIsDiscoverable(value);
  }

  get example(): string {
    return this.phrase.getExample();
  }

  set example(value: string) {
    this.phrase.setExample(value);
  }

  remove(phrase: Translation): void {
    const index = this.phrase.getTranslationList().indexOf(phrase);

    if (index >= 0) {
      const translations = this.phrase.getTranslationList();
      translations.splice(index, 1);
      this.phrase.setTranslationList(translations);
    }
  }

  saveEnabled(): boolean {
    return !!this.phrase && !!this.phrase.getNormalizedText().trim() && !!this.phrase.getTranslationList().length && this.uniqueOk();
  }

  uniqueOk(): boolean {
    if (!this.isAdd) {
      return true;
    }
    return !this.collectionService.get().getPhraseList().map(e => e.getNormalizedText()).includes(this.phrase.getNormalizedText().trim());
  }

  save(): void {
    this.backend.addPhrase(this.collectionName, this.phrase).subscribe(response => {
      if (this.isAdd) {
        this.phrase = new Phrase();
        this.phrase.setSourceLanguage(this.collectionService.get().getSourceLanguage());
        this.phrase.setIsDiscoverable(true);
      } else {
        this.router.navigateByUrl(`/list/${this.collectionName}`);
      }
    });
  }

}
