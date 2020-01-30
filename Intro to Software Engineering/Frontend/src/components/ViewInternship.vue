<template>
  <div id="Cooperator">
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
    <div>
      <div>
      <table width = "50%" align = "center">
        <p></p>
      <b-tabs content-class="mt-3" id="tab" variant="light" >
        <b-tab title="Internship Information" active>
    <b-jumbotron variant="light">

    <template slot="header">{{internships[0].company}}</template>




    <template slot="lead">

        <p></p>
      <div>
        <div>
          <b-card-group deck>

            <b-card bg-variant="light" text-variant="dark" header="Employer"  class="card header-danger mb-1">
              <b-card-text>{{internships[0].employer}}</b-card-text>
            </b-card>

            <b-card bg-variant="light" text-variant="dark" header="Term" class="card header-danger mb-1">
              <b-card-text>{{internships[0].term}}{{internships[0].year}}</b-card-text>
            </b-card>

            <b-card bg-variant="light" text-variant="dark" header="Status" class="card header-danger mb-1">
          <b-card-text>Completed: {{internships[0].completed}}</b-card-text>
            </b-card>
          </b-card-group>

        </div>

      </div>



    </template>

    <hr class="my-4">

    <b-button variant="danger" disabled v-if="internships[0].completed" @click="finishInternship(),  $router.push({ path: `/user/`+$route.params.id })">Finish Internship</b-button>
    <b-button variant="danger" v-else @click="finishInternship(),  $router.push({ path: `/user/`+$route.params.id })">Finish Internship</b-button>
  </b-jumbotron>
        </b-tab>
        <b-tab title="Form Submission">

            <div id = "Documents">
              <b-progress variant="danger" :value="internships[0].documents.length" :max=4 show-progress animated />
              <p></p>
            </div>
          <div align = "left" id = "form">
            <b-form  >
              <b-form-group id="exampleInputGroup1" label="Document URL:" label-for="exampleInput1">
                <b-form-input
                  id="exampleInput1"
                  type="url"
                  v-model="form.url"
                  required
                  placeholder="Enter Document URL" />
                      <p></p>
              </b-form-group>

              <b-form-group id="exampleInputGroup2" label="Your Document Name" label-for="exampleInput2">
                <b-form-input
                  id="exampleInput2"
                  type="text"
                  v-model="form.docname"
                  required
                  placeholder="Enter Document Name" />
                      <p></p>
              </b-form-group>



        <table align = "center">

            <b-button  variant="primary" disabled v-if="internships[0].completed" @click="submitInternshipEvaluation(form.url, form.docname)">Submit</b-button>
            <b-button  variant="primary" v-else @click=" submitInternshipEvaluation(form.url, form.docname)">Submit</b-button>
            <b-button v-b-toggle.collapse-1 variant="danger">Show Submitted Documents</b-button>
            <b-collapse id="collapse-1" class="mt-2">
              <b-card v-for="document in internships[0].documents">
                <p class="card-text">Document Name: {{document.name}}  </p>
                <p class ="card-text">Document URL: {{document.url}} </p>
                <b-button variant="danger" @click="deleteDocumentInternship(document.url)">Delete Document</b-button>
              </b-card>
            </b-collapse>
            </table>

            </b-form>
          </div>
            <span v-if="errorDocument" style="color:red">Error: {{errorDocument}}</span>









        </b-tab>
      </b-tabs>
    </table>
    </div>
  </div>
  </div>

</template>


<script src="./viewinternship.js">
</script>


<style>
#Cooperator {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
}
h2 {
  text-align: center;
}
#tab{

}
</style>
