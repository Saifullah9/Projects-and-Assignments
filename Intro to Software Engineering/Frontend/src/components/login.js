
// imports
import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.build.host + ':' + config.build.port
var backendUrl = 'https://' + config.build.backendHost + ':' + config.build.backendPort

var AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})
//initialization of the variable and methods
export default {
  name: "login",
  data() {
    return {
      actualpassword: '',
      errorlogin: '',
      errorperson: '',
      students: '',
      message: '',
      form: {
        password: '',
        studentId: '',

      },
    };


  },
  methods: {
    // on login function that calls the get student and get password using a http request
    login: function () {
      var SID = this.form.studentId;

      // Initializing people from backend
        this.$nextTick(() => {
      //Gets student by student ID (GET Request)
      AXIOS.get(`/getStudent/` + SID)
      .then(response => {
        // JSON responses are automatically parsed.
        this.students = response.data
        this.errorlogin = ''

        AXIOS.get(`/getPassword/` + SID)
        .then(response => {
          console.log(response);
          // JSON responses are automatically parsed.
          this.actualpassword = response.data;
        })
        .catch(e => {
          var errorMsg =  e.response.data.message
        })
        .finally(() => {
          if (this.form.password === this.actualpassword){
            this.$nextTick(() => {
              this.$router.push({ path: `/user/` + SID });
            });
          } else {
            this.errorlogin = 'ID and password do not match.'
          }
        });

        AXIOS.get(`/getStudent/` + SID)
        .then(response => {
          // JSON responses are automatically parsed.
          this.$nextTick(() => {
            this.students = response.data
            this.errorPerson = ''
          });
        })
        .catch(e => {
          var errorMsg =  e.response.data.message
          console.log(errorMsg)
          this.errorPerson = errorMsg;
        });

      })
      .catch(e => {
        var errorMsg =  e.response.data.message
        console.log(errorMsg)
        this.errorlogin = errorMsg;
      });

      });

    },

    onRegister(evt) {
    },
    onLogin(evt) {
    },
    // onclick method that resets the password and emails it to you
    onClick() {
      AXIOS.get(`/getStudent/` + this.ID)
      .then(response => {
        // JSON responses are automatically parsed.
        this.$nextTick(() => {
          this.students = response.data
          this.errorPerson = ''
        });
      })
      .catch(e => {
        var errorMsg =  e.response.data.message
        console.log(errorMsg)
        this.errorPerson = errorMsg;
      })
      .finally(() => {

        // If I can actually manage to find the student with this ID,
        // then I want to generate a new password, set it, and send an email with the new password


          AXIOS.post(`/setPassword/` + this.ID)
          .then(response => {
            console.log(response);
            // JSON responses are automatically parsed.
            this.actualpassword = response.data;
          })
          .catch(e => {
            var errorMsg =  e.response.data.message
          })
          .finally(() => {
            var Email =
            { send: function (a) {
              return new Promise(function (n, e)
              { a.nocache = Math.floor(1e6 * Math.random() + 1),
                a.Action = "Send"; var t = JSON.stringify(a);
                 Email.ajaxPost("https://smtpjs.com/v3/smtpjs.aspx?", t,
              function (e) { n(e) }) }) },
               ajaxPost: function (e, n, t) {
              var a = Email.createCORSRequest("POST", e);
             a.setRequestHeader("Content-type", "application/x-www-form-urlencoded", 'Access-Control-Allow-Origin'),
                a.onload = function () {
                   var e = a.responseText; null != t && t(e) },
                a.send(n) },
                 ajax: function (e, n) {
                  var t = Email.createCORSRequest("GET", e);
                   t.onload = function () {
                     var e = t.responseText; null != n && n(e) },
                      t.send() },
               createCORSRequest: function (e, n) {
            var t = new XMLHttpRequest;
            return "withCredentials" in t ? t.open(e, n, !0) : "undefined" != typeof XDomainRequest ? (t = new XDomainRequest).open(e, n) : t = null, t } };
            Email.send({
                Host : "smtp.elasticemail.com",
                Username : "saifnodi@gmail.com",
                Password : "db793403-d952-4b1c-92f8-a6d6575a8d81",
                To : this.students.person.email,
                From : "saifnodi@gmail.com",
                Subject : "Cooperator Website Password Reset",
                Body : 'Your password is ' + this.actualpassword + '.'
            }).then(
              message => alert(message)
            );
          });
      });
        // Close the menu and (by passing true) return focus to the toggle button
        this.$refs.dropdown.hide(true)
      }


  },


};
