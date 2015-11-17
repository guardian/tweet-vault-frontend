var twitterUsers = new Bloodhound({
  datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
  queryTokenizer: Bloodhound.tokenizers.whitespace,
  //prefetch: '../data/films/post_1960.json',
  local: ['hey', 'ya'],
  remote: {
    url: '/users?q=%QUERY',
    wildcard: '%QUERY'
  }
});

$('#twitter-username').typeahead(null, {
  name: 'users',
  display: 'screenName',
  source: twitterUsers
});