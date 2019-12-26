<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="../../resources/js/jquery-3.4.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/commonUtils.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/js.cookie-2.2.1.min.js" />"></script>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
</head>
<body>
<h2>Login Page</h2>
<div>
    <table>
        <tr>
            <td>login:</td>
            <td><input type='text' name='username'></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='password' /></td>
        </tr>
        <tr>
            <div class="g-recaptcha" data-size="compact" data-sitekey="${siteCode}" data-callback="saveCaptchaResponse"></div>
            <input type="hidden" name="captchaToken"/>
        </tr>
        <tr>
            <td colspan='2'><input name="submit" type="submit" value="submit" /></td>
            <td><a href="/editprofile" target="_self">Register</a></td>
        </tr>
    </table>
</div>
</body>
<script type="text/javascript">
    saveTokenToCookies(null);
    $('input[type=submit]').click(function () {
        var login = $('input[name=username]').val();
        var pass = $('input[name=password]').val();
        var captchaToken = $('input[name=captchaToken]').val();
        $.ajax({
            method: 'POST',
            url: '/oauth/token',
            headers: { 'Authorization': getCode(), 'captchaToken': captchaToken},
            data: jQuery.param({ grant_type: 'password', username : login, password: pass, scope: 'openid'}),
            success: function(resultData){
                var token = resultData.access_token;
                saveTokenToCookies(token);
                redirectTo('/newslist');
            }
            ,
            error: function (xhr, resp, text) {
                $('input[name=username]').val(null);
                $('input[name=password]').val(null);
                alert(xhr.responseJSON.message);
            }
        });
    });
    function saveCaptchaResponse(token)
    {
        $('input[name=captchaToken]').val(token);
        console.log(token);
    }
</script>