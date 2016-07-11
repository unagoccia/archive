export const htmlTemplate = `

<!-- Fixed navbar -->
<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a [routerLink]="['/home']" class="navbar-brand">OSM-sample</a>
    </div>
    <div id="navbar" class="collapse navbar-collapse">
      <ul class="nav navbar-nav">
        <li>
            <a [routerLink]="['/home']">Home</a>
        </li>
        <li>
            <a [routerLink]="['/one']">データ可視化</a>
        </li>
        <li>
            <a [routerLink]="['/two']">ユーザ管理</a>
        </li>
      </ul>
    </div><!--/.nav-collapse -->
  </div>
</nav>

<router-outlet></router-outlet>

<footer class="footer">
  <div class="container">
    <p class="text-muted">Grails 3 Angular 2 Starter</p>
  </div>
</footer>

`;
