var api = Vue.resource('/test')

Vue.component('some-list', {
  data: function() {
    return {
        element: null
    }
  },
    props: ['list'],
    template: '<div>'
        + '<input-form :list="list"/>'
        +'<element-view v-for="element in list" :element="element" :list="list"/>'
        +'</div>',
    created: function() {
    api.get().then(res =>
        res.json().then(data =>
            data.forEach(element => this.list.push(element))
        )
    )
   }
});

Vue.component('element-view', {
    props: ['element', 'list'],
    template: '<li>'
        + '{{ element.value }}' + '<input type="button" text="Delete" @click="del" />'
        + '</li>',
    methods: {
        del: function() {
            var value = this.element.value;
            api.remove(value).then(res => {
                if(res.ok)
                    this.list.splice(this.list.indexOf(this.element), 1);
            });
        }
    }
});

Vue.component('input-form', {
    props: ['list'],
    data: function() {
        return {
            value: ''
        }
    },
    template: '<div>'
        + '<input type="text" placeholder="Enter your element" v-model="value" />'
        + '<input type="button" value="Save" @click="save" />'
        + '<input type="button" value="Edit" @click="edit" />'
        + '</div>',
    methods: {
        save: function() {
                var value = this.value;
                api.save(value).then(res => {
                    res.json().then(data => {
                    console.log(this.list);
                        this.list.push(data);
                        this.value = '';
                    })
                });
        }
        edit: function() {

        }
    }
});

var app = new Vue({
  el: '#app',
  template:
            '<some-list :list="list" />',
  data: {
    list : []
  }
})