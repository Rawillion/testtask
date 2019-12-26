<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="../../resources/js/jquery-3.4.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/commonUtils.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/js.cookie-2.2.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/moment.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/graphql.js" />"></script>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Person List</title>
</head>
<body>
<h2>Profile</h2>
<a id="logout" href="/" target="_self">Logout</a>
<a id="tonewslist" href="/newslist" target="_self">News List</a>
    <div block="login">
        <div>Login:</div>
        <div><input path="login" type='text' name='login'/></div>
    </div>
    <div block="password">
        <div>Password:</div>
        <div><input path="password" type='password' name='password' /></div>
    </div>
    <div block="firstName">
        <div>First Name:</div>
        <div><input path="firstName" type='text' name='firstName'/></div>
    </div>
    <div block="lastName">
        <div>Last Name:</div>
        <div><input path="lastName" type='text' name='lastName' /></div>
    </div>
    <div block="email">
        <div>Email:</div>
        <div><input path="email" type='text' name='email'/></div>
    </div>
    <div block="birthday">
        <div>Birthday:</div>
        <div><input path="birthday" type='text' name='birthday'/></div>
    </div>
    <div block="categories">
        <div>News Category:</div>
        <div><select multiple="multiple" name="categories"></select></div>
    </div>
    <div block="image">
        <div>Profile Image:</div>
        <img src="" id="imageview">
        <div><input path="file" type='file' name='image' accept=".jpg, .jpeg"/></div>
    </div>
    <div block="gender">
        <div>Gender:</div>
        <div><input path="gender" type='text' name='gender'/></div>
    </div>
    <div block="registrationDate">
        <div>Registration Date:</div>
        <div><input path="registrationDate" type='text' name='registrationDate' readonly/></div>
    </div>
    <div block="recaptcha">
        <div class="g-recaptcha" data-size="compact" data-sitekey="${siteCode}" data-callback="saveCaptchaResponse"></div>
        <input type="hidden" name="captchaToken"/>
    </div>
    <div>
        <input name="submit" type="submit" value="submit" />
    </div>

</body>

</html>
<script type="text/javascript">
    $('input[type=file]').on("change", function () {
        var reader = new FileReader();
        reader.onload = function (e) { $('#imageview').attr('src', e.target.result); }
        reader.readAsDataURL($('input[type=file]')[0].files[0]);
    });
    //categories
    $.ajax({
        url: '/newsCategories',
        method: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function(e) {
            $.each(e._embedded.newsCategories, function (index, item) {
                $('select[name=categories]').append($('<option>', {
                    text: item.name,
                    value: item._links.self.href.split('/')[item._links.self.href.split('/').length - 1]
                }));
            });
        },
        error: function(xhr, resp, text) {
            console.log(xhr, resp, text);
        }
    });
    if (getTokenFromCookies() == 'null')
    {
        $('#logout').text('Back to Login Page');
        $('[block=firstName]').hide();
        $('[block=lastName]').hide();
        $('[block=email]').hide();
        $('[block=birthday]').hide();
        $('[block=categories]').hide();
        $('[block=image]').hide();
        $('[block=gender]').hide();
        $('[block=registrationDate]').hide();
    }
    else
    {
        var graph = graphql('/graphql',
            {
                method: 'POST',
                asJSON: true,
                headers: {'Authorization': 'Bearer ' + getTokenFromCookies()},
                debug: true
            });
        $('[block=login]').hide();
        $('[block=password]').hide();
        $('[block=recaptcha]').hide();
        var getUserInfoRequest = "{currentUser{firstName, lastName, email, birthday, newsCategories, email, image, gender, registrationDate}}";
        graph.query(getUserInfoRequest, {"empty":"var"}).then(function (response) {
            console.log(response.currentUser);
            $('input[name=firstName]').val(response.currentUser.firstName);
            $('input[name=lastName]').val(response.currentUser.lastName);
            $('input[name=email]').val(response.currentUser.email);
            $('input[name=birthday]').val(response.currentUser.birthday != -1 ? moment(response.currentUser.birthday).format('YYYY-MM-DD') : '');
            for (var i = 0; i< response.currentUser.newsCategories.length; ++i)
                $('select[name=categories] option[value=' + response.currentUser.newsCategories[i] + ']').attr('selected','selected');
            if (response.currentUser.image != null)
            {
                $('#imageview').attr('src', 'data:image/jpeg;base64,' + response.currentUser.image);
            }
            $('input[name=gender]').val(response.currentUser.gender);
            $('input[name=registrationDate]').val(response.currentUser.registrationDate != -1 ? moment(response.currentUser.registrationDate).format('YYYY-MM-DD') : '');
        }).catch(function (error) {
            console.log(error)
        })
    }
    $('input[name=submit]').click(function () {
        if (getTokenFromCookies() == 'null') {
            var captchaToken = $('input[name=captchaToken]').val();
            var createData = {
                "login": $('input[name=login]').val(),
                "password": $('input[name=password]').val()
            };
            $.ajax({
                url: '/user',
                method: 'POST',
                headers: {'captchaToken': captchaToken},
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(createData),
                success: function () {
                    redirectTo('/login');
                },
                error: function (xhr, resp, text) {
                    console.log(xhr, resp, text);
                    alert(xhr.responseJSON.message);
                }
            })
        }
        else
        {
            var categories = [];
            var selected = $('select[name=categories]').find('option:selected');
            for (var i = 0; i < selected.length; ++i)
            {
                categories.push(selected[i].value);
            }
            var imageContent = '';
            var imageName = '';
            if ($('input[type=file]').val() != "")
            {
                imageContent =  $('#imageview').attr('src').split(',')[1];
                imageName = $('input[type=file]')[0].files[0].name;
            }
            var userData = {
                "firstName": $('input[name=firstName]').val(),
                "lastName": $('input[name=lastName]').val(),
                "email": $('input[name=email]').val(),
                "birthday": moment($('input[name=birthday]').val(), 'YYYY-MM-DD').valueOf(),
                "image" : imageContent,
                "imageName": imageName,
                "newsCategories": categories,
                "gender": $('input[name=gender]').val()
            };
            var graph = graphql('/graphql',
                {
                    method: 'POST',
                    asJSON: true,
                    headers: {'Authorization': 'Bearer ' + getTokenFromCookies()},
                    debug: true
                });
            var updateUserRequest = 'MutationUser(@autodeclare){updateUser(updatingInfo: $userinfo)}';
            graph.mutate(updateUserRequest,
                {
                    "userinfo!userInputInfoDto": userData
                })
                .then(function (response) {
                    redirectTo('/newslist');
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    });
    function saveCaptchaResponse(token)
    {
        $('input[name=captchaToken]').val(token);
        console.log(token);
    }
</script>