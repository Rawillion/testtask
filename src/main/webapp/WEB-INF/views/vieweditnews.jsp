<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="../../resources/js/jquery-3.4.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/commonUtils.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/js.cookie-2.2.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/moment.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/graphql.js" />"></script>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8" />
    <title>News Page</title>
    <a id="tonewslist" href="/newslist" target="_self">Back To News List</a>
</head>
<body>
<h2 id="header">News:</h2>
<div block="text">
    <div>Title:</div>
    <div><input path="title" type='text' name='title' /></div>
</div>
<div block="text">
    <div>Text:</div>
    <div><input path="text" type='text' name='text' /></div>
</div>
<div block="category">
    <div>News Category:</div>
    <div><select name="categories"></select></div>
</div>
<div block="image">
    <div>News Image:</div>
    <img src="" id="imageview">
    <div><input path="file" type='file' name='image' accept=".jpg, .jpeg" /></div>
</div>
<div block="creationDate">
    <div>Creation Date:</div>
    <div><input path="creationDate" type='text' name='creationDate' readonly/></div>
</div>
<div block="submit">
    <input name="submit" type="submit" value="Save" />
</div>

</body>
</body>
</html>
<script type="text/javascript">
    var action = getUrlParameter('action');
    var newsId = getUrlParameter('id');
    if (action == undefined || action == '')
        action = 'view';
    if (newsId == undefined || newsId == '')
        newsId = -1;
    if (action == 'view' && newsId == -1)
        redirectTo('/newslist');
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
    if (newsId != -1)
    {
        var graph = graphql('/graphql',
            {
                method: 'POST',
                asJSON: true,
                headers: {'Authorization': 'Bearer ' + getTokenFromCookies()},
                debug: true
            });
        var getNews = "($newsId: Long){getNews(id : $newsId){title, text, newsCategory, image, creationDate}}";
        graph.query(getNews, {"newsId":newsId}).then(function (response) {
            console.log(response);
            $('input[name=title]').val(response.getNews.title);
            $('input[name=text]').val(response.getNews.text);
            $('input[name=creationDate]').val(moment(response.getNews.creationDate).format('YYYY-MM-DD'));
            $('#imageview').attr('src', 'data:image/jpeg;base64,' + response.getNews.image);
            $('select[name=categories] option[value=' + response.getNews.newsCategory + ']').attr('selected', 'selected');
        }).catch(function (error) {
            console.log(error)
        })
    }
    if (action == 'view')
    {
        $('[block=submit]').hide();
        $('input[name=title]').attr('readonly', 'readonly');
        $('input[name=text]').attr('readonly', 'readonly');
        $('select[name=categories]').attr("disabled", true);
        $('input[name=image]').hide();

        $('#header').html('View News');
    }
    else
        $('#header').html('Edit News');
    if (newsId == -1)
        $('[block=creationDate]').hide();

    function getUrlParameter(sParam) {
        var sPageURL = window.location.search.substring(1),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
            }
        }
    }

    $('input[name=submit]').click(function () {
        var graph = graphql('/graphql',
            {
                method: 'POST',
                asJSON: true,
                headers: {'Authorization': 'Bearer ' + getTokenFromCookies()},
                debug: true
            });
        var imageContent = '';
        var imageName = '';
        if ($('input[type=file]').val() != "")
        {
            imageContent =  $('#imageview').attr('src').split(',')[1];
            imageName = $('input[type=file]')[0].files[0].name;
        }
        var newsData = {
            "id": newsId,
            "title": $('input[name=title]').val(),
            "text": $('input[name=text]').val(),
            "newsCategory": $('select[name=categories]').find('option:selected')[0].value,
            "imageName": imageName,
            "image": imageContent
        };
        var updateOrCreateNews = "MutationNews(@autodeclare){updateOrCreateNews(newsInput : $newsinfo)}";
        var updateUserRequest = 'MutationUser(@autodeclare){updateUser(updatingInfo: $userinfo)}';
        graph.mutate(updateOrCreateNews,
            {
                "newsinfo!newsInputDto": newsData
            })
            .then(function (response) {
                console.log(response);
                redirectTo('/newslist');
            })
            .catch(function (error) {
                console.log(error);
            });
    });
    $('input[type=file]').on("change", function () {
        var reader = new FileReader();
        reader.onload = function (e) { $('#imageview').attr('src', e.target.result); }
        reader.readAsDataURL($('input[type=file]')[0].files[0]);
    });
</script>