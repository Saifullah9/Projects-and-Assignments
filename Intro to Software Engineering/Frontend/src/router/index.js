import Vue from 'vue'
import Router from 'vue-router'
import Cooperator from '@/components/Cooperator'

Vue.use(Router)


export default new Router({
  routes: [
    {
      path: '/register',
      name: 'Cooperator',
      component: require('../components/Cooperator.vue').default
    },
    {
      path: '/',
      name: 'Login',
      component: require('../components/Login.vue').default
    },
    {
      path: '/user/:id',
      name: 'User',
      component: require('../components/User.vue').default
    },

    {
      path: '/user/:id/registerforinternship',
      name: 'registerforinternship',
      component: require('../components/RegisterForInternship.vue').default
    },
    {
      path: '/user/:id/create-offer',
      name: 'CreateOffer',
      component: require('../components/CreateOffer.vue').default
    },
    {
      path: '/user/:id/viewInternships/:internshipId',
      name: 'ViewInternships',
      component: require('../components/ViewInternship.vue').default
    },
  ]
})
