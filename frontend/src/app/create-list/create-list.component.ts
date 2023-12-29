import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router} from "@angular/router";
import {Language, LanguageMap} from "../../proto/language_pb";
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {BackendService} from "../backend.service";

@Component({
  selector: 'app-create-list',
  standalone: true,
  imports: [CommonModule, MatInputModule, MatSelectModule, FormsModule, MatButtonModule],
  templateUrl: './create-list.component.html',
  styleUrl: './create-list.component.css'
})
export class CreateListComponent {
  name!: string;

  srcLanguage!: LanguageMap[keyof LanguageMap];
  dstLanguage: LanguageMap[keyof LanguageMap] = Language.RU;

  srcLangs = [
    {name: 'English', value: Language.EN_US},
    {name: 'Deutsch', value: Language.DE},
    {name: 'French', value: Language.FR},
    {name: 'Polish', value: Language.PL},
    {name: 'Italian', value: Language.IT},
  ];
  dstLangs = [
    {name: 'Русский', value: Language.RU},
    {name: 'English', value: Language.EN_US},
  ];

  constructor( private router: Router, private backend: BackendService) { }

  save(): void {
    this.backend.createCollection(this.name.trim(), this.srcLanguage, this.dstLanguage)
    .subscribe(unused => {
      this.router.navigateByUrl('/lists');
    });
  }

  saveEnabled(): boolean {
    return !!this.name && !!this.name.trim() && !!this.srcLanguage && !!this.dstLanguage;
  }
}
