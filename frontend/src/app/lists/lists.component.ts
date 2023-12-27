import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router} from '@angular/router';
import {BackendService} from '../backend.service';
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-lists',
  standalone: true,
  imports: [CommonModule, MatListModule, MatIconModule, MatButtonModule],
  templateUrl: './lists.component.html',
  styleUrls: ['./lists.component.css']
})
export class ListsComponent {

  private collectionNames: Array<string> = [];

  constructor(backend: BackendService,
              private router: Router) {
    backend.loadCollections().subscribe((data) => this.collectionNames = data.getNamesList());
  }

  getNames(): Array<string> {
    return this.collectionNames;
  }

  isComponentLoaded(): boolean {
    return !!this.collectionNames;
  }

  goToCollectionDetails(name: string) {
    this.router.navigateByUrl(`/list/${name}`);
  }

  goToExam(name: string) {
    this.router.navigateByUrl(`/exam/${name}`);
  }

  addCollection() {
    this.router.navigateByUrl('/lists/add');
  }
}
