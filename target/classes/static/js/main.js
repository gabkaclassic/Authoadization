var api = Vue.resource('/test')

Vue.component('some-list', {
    props: ['list'],
    template: '<div><li v-for="element in list">{{element}}</li></div>',
    created: function() {
    api.get().then(res =>
        res.json().then(data =>
            data.forEach(element => this.list.push(element))
        )
    )
   }
});


var app = new Vue({
  el: '#app',
  template: '<some-list :list="list" />',
  data: {
    list : []
  }
})