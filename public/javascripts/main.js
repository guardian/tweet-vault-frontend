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

function refreshTweets(user) {
  $('.user-tweets').load('/get-tweets-html?user='+user);
}

$('#twitter-username').typeahead(null, {
  name: 'users',
  display: 'screenName',
  source: twitterUsers
});

$('.twitter-users').click('.twitter-user', function(e) {
  $(e.target).addClass('active');
  $(e.target).siblings().removeClass('active');
  refreshTweets($(e.target).text());
});

$('.refresh-tweets').click(function() {
  refreshTweets($('.twitter-user.active').text());
});

$('.add-user').click(function() {
  $.get('/add-user?user='+$('#twitter-username').val()).done(function() {
    setTimeout(function() {
      $('.twitter-users').load('/get-users-html');
    }, 1000);
  })
});