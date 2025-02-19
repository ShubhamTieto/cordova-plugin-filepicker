var exec = require('cordova/exec');
var FilePicker = {
    pickFile: function (success, error) {
        exec(success, error, 'FilePicker', 'pickFile', []);
    }
};
module.exports = FilePicker;
