
<template>
  <!--Declaring a table with fields-->
  <table width = "50%" align = "center">
  <div align = "left" id = "form">

    <!--On-clicking the Submit button, all field values are posted to the system-->
    <!--On-clicking the Reset button, all field values are set to ""-->
    <b-form @submit="onSubmit" @reset="onReset" v-if="show" >

      <!--Creates the email field -->
      <b-form-group
        id="exampleInputGroup1"
        label="Email address:"
        label-for="exampleInput1"
        description="We'll never share your email with anyone else."
      >
        <b-form-input
          id="exampleInput1"
          type="email"
          v-model="form.email"
          required
          placeholder="Enter email" />
      </b-form-group>

      <!--Creates the field for student's First Name -->
      <b-form-group id="exampleInputGroup2" label="Your First Name:" label-for="exampleInput2">
        <b-form-input
          id="exampleInput2"
          type="text"
          v-model="form.firstname"
          required
          placeholder="Enter first name" />
      </b-form-group>

        <!--Creates the field for student's Last Name -->
      <b-form-group id="exampleInputGroup3" label="Your Last Name:" label-for="exampleInput3">
        <b-form-input
          id="exampleInput3"
          type="text"
          v-model="form.lastname"
          required
          placeholder="Enter last name" />
      </b-form-group>

      <!--Creates the field for student ID-->
      <b-form-group id="exampleInputGroup4" label="Your Student ID:" label-for="exampleInput4">
        <b-form-input
          id="exampleInput4"
          type="number"
          v-model="form.studentId"
          required
          placeholder="Enter Student ID" />
      </b-form-group>

<!-- Aligns all fields to the center of the webpage-->
<table align = "center">
      <b-button type="submit" variant="primary" >Submit</b-button>
      <b-button  variant="danger" @click="sendEmail(password.data)">Send Me a Password!</b-button>
      <b-button type="reset" variant="danger">Reset</b-button>
      <p v-if="isRegistered === true">
          Your password is: {{password.data}}. To have this sent to your email as a reminder, please press the 'Send Me a Password' button.
      </p>

      <span v-if="errorPerson != ''" style="color:red"> {{errorPerson}}</span>
      <span v-if="errorPerson === ''" style="color:green"> {{messagePerson}}</span>
    </table>

    </b-form>
  </div>
    </table>
</template>

<!--This file is bound to the cooperator.js file-->
<script src = "./cooperator.js">

  </script>
  <style>
  </style>
