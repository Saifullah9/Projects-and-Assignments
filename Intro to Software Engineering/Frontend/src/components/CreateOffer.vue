<template>
  <div id="Cooperator">
    <b-navbar toggleable="lg" type="dark" variant="danger">
      <b-navbar-brand href="#" @click="$router.push({ path: `/user/`+$route.params.id})">McGill Cooperator Service</b-navbar-brand>

      <b-navbar-toggle target="nav_collapse" />

      <b-collapse is-nav id="nav_collapse">
        <b-navbar-nav>

          <b-nav-item disabled @click="$router.push({ path: `/user/`+$route.params.id+`/create-offer`})">Offer Management</b-nav-item>
          <b-nav-item @click="$router.push({ path: `/user/`+$route.params.id+`/registerforinternship`})">Register for an Internship</b-nav-item>
          <b-nav-item @click="$router.push({ path: `/`})">Sign Out</b-nav-item>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <div v-if="errorOffers1">
      <p>Click below to initialize your internship offer:</p>
      <b-button @click="createOffer()" variant="danger">Create Offers</b-button>
            <p></p>

<div v-for="offer in alloffers" :key="key">
            <b-card-group columns>
        <b-card v-for="something in offer"

          img-src="https://picsum.photos/600/300/?image=8"
          img-alt="Image"
          img-top
          tag="article"
          style="max-width: 20rem;"
          class="mb-2"
          v-if= "something.validated"
        >
        <table>
          <td >
          <b-card-text >Offer ID: {{something.offerId}}</b-card-text>
          <b-card-text>Valid: {{something.validated}} </b-card-text>
          </td>
          <td>
          <div v-if="offer[offer.length -1] === something ">
            <b-button variant="secondary" @click="cancelOffer()">Cancel offer</b-button>
          </div>
          </td>


        </table>
        </b-card>
          </b-card-group>
</div>


      <p></p>
      <span v-if="errorOffers" style="color:red">Error: {{errorOffers}}</span>

    </div>

    <div v-else>
      <p>Submit the relevant forms below.</p>
      <table width = "50%" align = "center">
        <div>
          <b-progress variant="danger":value="Offers[0].documents.length" :max=2 show-progress animated />
          </div>
      <div align = "left" id = "form">
        <b-form  @submit="onSubmit" >
          <b-form-group id="exampleInputGroup1" label="Document URL:" label-for="exampleInput1">
            <b-form-input
              id="exampleInput1"
              type="text"
              v-model="form.url"
              required
              placeholder="Enter Document URL" />
          </b-form-group>

          <b-form-group id="exampleInputGroup2" label="Your Document Name" label-for="exampleInput2">
            <b-form-input
              id="exampleInput2"
              type="text"
              v-model="form.docname"
              required
              placeholder="Enter Document Name" />
          </b-form-group>



    <table align = "center">
        <span v-if="errorOffers" style="color:red">{{errorOffers}}</span>
          <b-button variant="danger" @click="validateOffer()">Validate Offer  </b-button>
          <b-button variant="secondary" @click="cancelOffer()">Cancel offer</b-button>
          <b-button type="submit" variant="primary" disabled v-if="Offers[0].documents.length===2">Submit</b-button>
          <b-button type="submit" variant="primary"  v-else>Submit</b-button>


  <b-button v-b-toggle.collapse-1 variant="dark">Show Submitted Documents</b-button>
  <b-collapse id="collapse-1" class="mt-2">
    <b-card v-for="document in Offers[0].documents">
      <p class="card-text">Document Name: {{document.name}}  </p>
      <p class ="card-text">Document URL: {{document.url}} </p>
      <b-button variant="danger" @click="deleteDocument(document.url)">Delete Document</b-button>
    </b-card>
  </b-collapse>

        </table>

        </b-form>
      </div>
        </table>


    </div>

  </div>
</template>


<script src="./createoffer.js">
</script>


<style>
#Cooperator {
  font-family: "Georgia", sans-serif;
}
h2 {
  text-align: center;
  color: #2c3e50;
}
</style>
