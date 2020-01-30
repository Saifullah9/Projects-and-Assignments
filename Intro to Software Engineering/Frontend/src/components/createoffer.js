//imports
import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.build.host + ':' + config.build.port
var backendUrl = 'https://' + config.build.backendHost + ':' + config.build.backendPort

var AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})



// constructors
class OfferDto{
  constructor(studentId) {
    this.studentId = studentID
    this.isActive = true
    this.isValidated = false
  }
}
// intialization of variables and methods
export default {
  name: "user",
  data() {
    return {
      key: 0,
      form: {
        url: '',
        docname: '',
      },
      Offers: [],
      alloffers: [],
      students:[],
      errorOffers: '',
      errorOffers1: '',
      newdocuments: [],
      error: 0,
      sID: '',
      response: []
    };
  },

  //Initialization function
  created: function() {

    var SID = this.$route.params.id;
    // Initializing people from backend

    //Gets student by student ID (GET Request)
    AXIOS.get(`/getStudent/` + SID)
    .then(response => {
      // JSON responses are automatically parsed.
      this.students = response.data
          this.errorOffers= ''
    })
    .catch(e => {
      var errorMsg =  e.response.data.message
      console.log(errorMsg)
      this.errorPerson = errorMsg;
    });

    //Gets offer by studentID (GET Request)
    AXIOS.get(`/getOffer/`+ SID, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.Offers.push(response.data)
      this.errorOffers= ''

    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorOffers1= errorMsg
    });

    AXIOS.get(`/getOffers/`+ SID, {}, {})
    .then(response => {
      // JSON responses are automatically parsed.
      this.alloffers.push(response.data)
      this.errorOffers= ''

    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorOffers1= errorMsg
    });
  },

  methods: {

    //Function creates offer (POST Request)
    createOffer: function() {
      var SID = this.$route.params.id;
      AXIOS.post(`/` + SID + `/create-offer/`)
      .then(response => {
        // JSON responses are automatically parsed.
        this.Offers.push(response.data)
        this.key += 1
        this.errorOffers = ''
        // this.$router.go(0)
        this.$nextTick(() => {
          window.history.go(0);
        });
        })
      .catch(e => {
        var errorMsg = e.response.data.message
        console.log(errorMsg)
        this.errorOffers = errorMsg
      });
    },

    //Function validates offer (POST Request)
    validateOffer: function() {
      var SID = this.$route.params.id;
      AXIOS.post(`/` + SID + `/validate-offer/`)
      .then(response => {
        // JSON responses are automatically parsed.
        this.$nextTick(() => {
          this.errorOffers = ''
          this.$router.go(0)
        });

      })
      .catch(e => {
        var errorMsg = e.response.data.message
        console.log(errorMsg)
        this.errorOffers = errorMsg
      });
  },

      /*Uploads Offer-Document on to the database (POST Request)
      *@param URL of Document, Name of Document
      */
      submitOfferDocument: function (documentURL, documentName) {
        var SID = this.$route.params.id
        AXIOS.post(`/`+SID+`/offer/upload-doc/?documentURL=`+documentURL+`&documentName=`+ documentName, {}, {})
        .then(response => {
        // JSON responses are automatically parsed.
        this.$nextTick(() => {
          this.newdocuments.push(response.data)
          newDocumentName: ''
          newDocumentURL: ''
          this.errorOffers= ''
          // this.$router.go(0)
        });
      })
      .catch(e => {
        var errorMsg = e.response.data.message
        console.log(errorMsg)
        this.errorOffers= errorMsg
      });
    },
    // deletes the document of the offer  using an Http Delete method
    deleteDocument: function (docUrl){

      var SID = this.$route.params.id
      AXIOS.delete(`/deleteDocument/`+SID+`/?docUrl=` + docUrl, {}, {})
      .then(response => {
      // JSON responses are automatically parsed.
      this.$nextTick(() => {

        this.errorOffers= ''
        this.$router.go(0)
      });
    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorOffers= errorMsg
    });
  },

    onValid(evt) {

     },

    //On-clicking Submit button, sets the offer-document fields (POST Request)
    onSubmit(evt) {
      var SID = this.$route.params.id
      //axios post method the updates the offer with the intended document
      AXIOS.post(`/`+SID+`/offer/upload-doc/?documentURL=`+this.form.url+`&documentName=`+this.form.docname, {}, {})
      .then(response => {
      evt.preventDefault()
      // JSON responses are automatically parsed.
      this.$nextTick(() => {
        this.newdocuments.push(response.data)
        newDocumentName: ''
        newDocumentURL: ''
        this.errorOffers= ''
      //  this.$router.go(0)
      });
    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorOffers= errorMsg
    })
    .finally(() => {
          // send an email  function from the google api
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
          Subject : "Cooperator Document Submission Confirmation",
          Body : 'This email is to confirm that your submission of ' +this.form.docname+ '(URL: '+ this.form.url + ') to your offer was successful.'
      }).then(
        message => alert(message)
      );
    });
  },
  // method that calls the cancel offer using a put http request.
  cancelOffer: function() {
    var SID = this.$route.params.id;
    AXIOS.put(`/` + SID + `/cancel-offer/`)
    .then(response => {
      // JSON responses are automatically parsed.
      this.$nextTick(() => {
        this.errorOffers = ''
        this.$router.go(0)
      });

    })
    .catch(e => {
      var errorMsg = e.response.data.message
      console.log(errorMsg)
      this.errorOffers = errorMsg
    });
}


  }
};
