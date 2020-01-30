<template>
  <div id="Cooperator">

    <!-- Declaring a navigation bar-->
    <b-navbar toggleable="lg" type="dark" variant="danger">
      <b-navbar-brand href="#" @click="$router.push({ path: `/user/`+$route.params.id})">McGill Cooperator Service</b-navbar-brand>

      <b-navbar-toggle target="nav_collapse" />

      <b-collapse is-nav id="nav_collapse">
        <b-navbar-nav>
          <b-nav-item @click="$router.push({ path: `/user/`+$route.params.id+`/create-offer`})">Offer Management</b-nav-item>
          <!-- Register for an Internship button, redirects user to the registration page, url: /user/(studentID)/registerforinternship -->
          <b-nav-item disabled @click="$router.push({ path: `/user/`+$route.params.id+`/registerforinternship`})">Register for an Internship</b-nav-item>
          <b-nav-item @click="$router.push({ path: `/`})">Sign Out</b-nav-item>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <h2>Registration</h2>
    <table width = "50%" align = "center">
    <div align = "left" id = "form">
      <b-form  >

        <b-form-group id="exampleInputGroup1" label="Internship ID" label-for="exampleInput1">
          <b-form-input
            id="exampleInput1"
            type="number"
            v-model="form.internid"
            required
            placeholder="Enter Internship ID" />
        </b-form-group>

        <b-form-group id="exampleInputGroup3" label="Employer Name" label-for="exampleInput3">
            <b-form-select v-model="selectedEmployer" :options="employerOptions">Employers</b-form-select>
        </b-form-group>

<table>
  <tr>

          <b-form-group label="Term">
            <b-form-radio-group v-model="selected" :options="options" name="radioInline" />
          </b-form-group>
          <div class="mt-3">Selected: <strong>{{ selected }}</strong></div>

          <b-form-group id="exampleInputGroup3" label="Internship Year" label-for="exampleInput3">
            <b-form-input
              id="exampleInput3"
              type="number"
              v-model="form.year"
              required
              placeholder="Enter Year of Internship" />
          </b-form-group>


        </tr>
</table>

  <table align = "center">
        <b-button variant="danger" @click="
        registerInternship(form.intenid, selectedEmployer[1], selectedEmployer[0],
          selected, form.year)">Register </b-button>

      </table>

      </b-form>
    </div>
          <span v-if="errorInternship" style="color:red">Error: {{errorInternship}}</span>
      </table>
  </div>
</template>


<script src="./registerforinternship.js">
</script>


<style>
#Cooperator {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
}
h2 {
  text-align: center;
}
</style>
