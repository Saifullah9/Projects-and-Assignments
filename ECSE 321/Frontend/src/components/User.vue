<template>
    <div id = "Cooperator">
      <b-navbar toggleable="lg" type="dark" variant="danger">
      <b-navbar-brand href="#" @click="$router.push({ path: `/user/`+$route.params.id})">McGill Cooperator Service</b-navbar-brand>
        <b-navbar-toggle target="nav_collapse" />
        <b-collapse is-nav id="nav_collapse">
          <b-navbar-nav>
            <b-nav-item @click="$router.push({ path: `/user/`+$route.params.id+`/create-offer`})">Offer Management</b-nav-item>
            <b-nav-item @click="$router.push({ path: `/user/`+$route.params.id+`/registerforinternship`})">Register for an Internship</b-nav-item>
            <b-nav-item @click="$router.push({ path: `/`})">Sign Out</b-nav-item>
          </b-navbar-nav>
        </b-collapse>
      </b-navbar>
      <div id = "Alerts">
      <b-alert show dismissible variant="dark">
    <p class="mb-0">
      {{message}}
      <div v-if="message1 != 'Nothing.'">
      <hr>
      {{message1}}
    </p>
  </div>
      </b-alert>

    </div>
            <div  id="Internships" v-for="SI in specificInternships">
              <b-card-group columns>
          <b-card v-for="something in SI"

            img-src="https://picsum.photos/600/300/?image=20"
            img-alt="Image"
            img-top
            tag="article"
            style="max-width: 20rem;"
            class="mb-2"
          >
            <b-card-text>{{something.company}}: {{something.term}} {{something.year}}</b-card-text>
            <b-button @click="$router.push({ path: `/user/`+$route.params.id+`/viewInternships/`+ something.internshipId })" variant="danger">View Internship</b-button>
            <!-- <b-button v-else @click="$router.push({ path: `/user/`+$route.params.id+`/viewInternships/`+ something.internshipId })" variant="primary">View Internship</b-button> -->
          </b-card>
            </b-card-group>
          </div>
</div>

</template>



<script src="./User.js">
</script>


<style>
#Cooperator {
  font-family: "HelveticaNeue", Helvetica, Arial, sans-serif;

}
#Alerts{
  width: 70%;
  text-align: center;
  margin: auto;
  padding: 10px;
  border: auto;
}
#Internships{
  text-align: center;
  margin: auto;
  padding: 30px;
  border: auto;
  top:100px;
  margin-bottom: 30px;
}
</style>
