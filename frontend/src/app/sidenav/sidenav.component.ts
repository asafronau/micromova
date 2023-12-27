import {MediaMatcher} from '@angular/cdk/layout';
import {ChangeDetectorRef, Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatListModule} from "@angular/material/list";
import {RouterLink, RouterOutlet} from "@angular/router";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-sidenav',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatSidenavModule,
    MatToolbarModule,
    RouterLink,
    RouterOutlet,
  ],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.css'
})
export class SidenavComponent {
  mobileQuery: MediaQueryList;

  private readonly mobileQueryListener: () => void;

  constructor(changeDetectorRef: ChangeDetectorRef, media: MediaMatcher) {
    this.mobileQuery = media.matchMedia('(max-width: 960px)');
    this.mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addEventListener('change', () => this.mobileQueryListener());
  }

  ngOnDestroy(): void {
    this.mobileQuery.removeEventListener('change', () => this.mobileQueryListener());
  }
}
