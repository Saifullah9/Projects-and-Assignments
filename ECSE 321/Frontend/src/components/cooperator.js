//constructor
class PersonDto {
  constructor(first, last, email) {
    this.first = first;
    this.last = last;
    this.email = email;
  }
}
// imports
let axios = require('axios');
var config = require('../../config')

var frontendUrl = 'http://' + config.build.host + ':' + config.build.port
var backendUrl = 'https://' + config.build.backendHost + ':' + config.build.backendPort
// setting axios headers
var AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})


class StudentDto {
  constructor(person, studentID) {
    this.studentID = studentID;
    this.person = person;
    this.termsRemaining = 4;
    this.termsFinished = 0;
  }
}
// Initialization of exported variables and methods
export default {
  name: "cooperator",
  data() {
    return {
      people: [],
      students: [],
      //password: '',
      newFirst: '',
      newLast: '',
      newEmail: '',
      employers: [],
      errorPerson: '',
      messagePerson: '',
      isRegistered: false,
    password:'',
      sID: '',
      response: [],
      form: {
        email: '',
        firstname: '',
        lastname: '',
        studentId: '',
      },
      show: true
    };


  },
  methods: {
    /*Function creates a student in the database (POST Request)
    *@param first name, last name, email, student ID
    */
    createStudent: function (first, last, email, sID) {
      AXIOS.post(`/create-student/?firstName=`+first+`&lastName=`+last+`&email=`+email+`&studentId=`+sID, {}, {})
      .then(response => {
        // JSON responses are automatically parsed.
        this.students.push(response.data)
      })      .catch(e => {
              var errorMsg =  e.response.data.message
              console.log(errorMsg)
              this.errorPerson = errorMsg
            });

    },

    //Sets the field values for a student, on clicking the submit button.
    onSubmit(evt) {
      evt.preventDefault()
    AXIOS.post(`/create-student/?firstName=`+this.form.firstname+`&lastName=`+this.form.lastname+`&email=`+this.form.email+`&studentId=`+this.form.studentId, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.$nextTick(() => {
        this.students.push(response.data)
        //this.$router.push({ path: `/` });
        AXIOS.get(`/getPassword/` + this.form.studentId)
        .then(response => {
          console.log(response);
          // JSON responses are automatically parsed.
          this.password = response;
          this.isRegistered = true
        })
        .catch(e => {
          var errorMsg =  e.response.data.message
        });
        this.$nextTick(()=> {
          if( this.password.data == ""){
            AXIOS.get(`/getPassword/` + this.form.studentId)
            .then(response => {
              console.log(response);
              // JSON responses are automatically parsed.
              this.password = response;

            })
            .catch(e => {
              var errorMsg =  e.response.data.message
            });
          }
      })
      });
        })
        .catch(e => {
            var errorMsg = e.response.data.message
            console.log(errorMsg)
            this.errorPerson = errorMsg
          });
        if (this.errorPerson === ''){
          this.messagePerson = 'Student account is successfully created!'
          this.isRegistered = true
          return 0;
        }
    },

    //Field values of the create-student form are set to ''
    onReset(evt) {
      evt.preventDefault()
      /* Reset our form values */
      this.form.email = ''
      this.form.firstname = ''
      this.form.lastname =''
      this.form.studentId = ''
      this.errorPerson =''
      /* Trick to reset/clear native browser form validation state */
      this.show = false
      this.$nextTick(() => {
        this.show = true
      })
    },
    // send an email  function from the google api
    sendEmail: function(passwordidiot) {
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
          To : this.form.email,
          From : "saifnodi@gmail.com",
          Subject : "Cooperator website Password",
          Body : 'Your password is: ' + passwordidiot + '.'
      }).then(
        message => alert(message)
      );
    }

  }
};
