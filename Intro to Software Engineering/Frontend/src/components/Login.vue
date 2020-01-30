<!-- Contents of the Login page-->
<template>
  <table width = "50%" align = "center">
  <div align = "left" id = "form">

    <!--On-clicking, the login button redirects user to the user's home page -->
    <!--On-clicking, the register button redirects user to the register page -->
    <b-form @login="onLogin" @register="onRegister" >

      <!--Student ID field-->
      <b-form-group id="exampleInputGroup1" label="Your Student ID:" label-for="exampleInput1" maxlength="10">
        <b-form-input
          id="exampleInput1"
          type="number"
          v-model="form.studentId"
          required
          placeholder="Enter Student ID" />
      </b-form-group>

      <!--Password field-->
      <b-form-group id="exampleInputGroup2" label="Your Password:" label-for="exampleInput2">
        <b-form-input
          id="exampleInput2"
          type="password"
          v-model="form.password"
          required
          placeholder="Enter Password" />
      </b-form-group>



<table align = "center">

      <!--On clicking the login button, user is redirected to url: '/user/(studentID)'-->
      <b-button type="login" variant="primary" @click="login()">Log In</b-button>
      <p>
      </p>
      <p></p>
      <span v-if="errorlogin" style="color:red">Error: {{errorlogin}}</span>
      <p>
        Don't have an Account?
      </p>

      <tr>

      <!--On clicking the register button, user redirected to url: '/register'-->
      <b-button type="register" variant="danger" @click="$router.push('register')">Register</b-button>
      <p></p>

      <div>
      <b-dropdown id="dropdown-form" text="Forgot Password?" ref="dropdown" class="m-2">
        <b-dropdown-form>

          <b-form-group label="Student ID" label-for="dropdown-form-password">
            <b-form-input
              id="StudentID"
              size="sm"
              v-model="ID"
            ></b-form-input>
          </b-form-group>
          <b-button variant="primary" size="sm" @click="onClick">New Password</b-button>
        </b-dropdown-form>
      </b-dropdown>
  </div>
    </tr>
    </table>
    </b-form>
  </div>
    </table>

</template>
<script src='https://www.google.com/recaptcha/api.js' async defer></script>
<script src = "./login.js">
  </script>
  <style>
  #dropdown-form{
    align-self: left;
  }
  </style>
