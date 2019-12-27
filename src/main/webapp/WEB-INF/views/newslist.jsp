<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="<c:url value="../../resources/js/jquery-3.4.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/commonUtils.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/js.cookie-2.2.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/pagination.js" />"></script>
<script type="text/javascript" src="<c:url value="../../resources/js/moment.min.js" />"></script>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8" />
    <title>News List</title>
    <a href="/" id="logout" target="_self">Logout</a>
    <a href="/editprofile" id="profile" target="_self">To Profile</a>
    <a href="/vieweditnews?action=edit&id=-1" target="_self">Create New</a>
</head>
<body>
<h1>News List</h1>
<input type="text" readonly name="categoriesNames" value="Available Categories: " style="width: 300px;">
<div id="newsContent"></div>
<div id="newsControls"></div>
</body>
</html>
<script type="text/javascript">
    var currentCategoriesIds = '';
    var currentCategoriesNames = '';
    $.ajax({
        url: '/currentCategories',
        method: 'GET',
        headers: {'Authorization': 'Bearer ' + getTokenFromCookies()},
        contentType: "application/json; charset=utf-8",
        async: false,
        success: function (e) {
            $.each(e, function (index, item) {
                currentCategoriesIds += item.id + ',';
                currentCategoriesNames += item.name + ',';
            });
        },
        error: function (xhr, resp, text) {
            redirectTo('/login');
        }
    });
    currentCategoriesIds = trimChar(currentCategoriesIds, ',');
    currentCategoriesNames = trimChar(currentCategoriesNames, ',');
    $('input[name=categoriesNames]').val($('input[name=categoriesNames]').val() + currentCategoriesNames);
    $('#newsControls').pagination({
        dataSource: '/news/search/findAllByNewsCategories?categories=' + currentCategoriesIds,
        locator: '_embedded.news',
        alias: {
            pageNumber: 'page',
            pageSize: 'size'
        },
        ajax: {
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + getTokenFromCookies());
            }
        },
        totalNumberLocator: function(response) {
            return response.page.totalElements;
        },
        callback: function(data, pagination) {
            var html = '';
            $.each(data, function (index, item) {
                html += template(item);
            });
            $('#newsContent').html(html);
            $.each(data, function (index, item) {
                var id = item._links.self.href.split('/')[4];
                $.ajax({
                    url: item._links.preview.href,
                    method: 'GET',
                    headers: {'Authorization': 'Bearer ' + getTokenFromCookies()},
                    contentType: "application/json; charset=utf-8",
                    success: function (e) {
                        loadImage(id, e._links.self.href.split('/')[4])
                    }
                });
            });
        }
    });
    function loadImage(id, imageId)
    {
        $.ajax({
            url: '/image/view/' + imageId,
            method: 'GET',
            headers: {'Authorization': 'Bearer ' + getTokenFromCookies()},
            contentType: "application/json; charset=utf-8",
            success: function (e) {
                $('#imagePreview' + id).attr('src', 'data:image/jpeg;base64,' + e);
            },
            error: function (xhr, resp, text) {
                console.log(xhr, resp, text);
            }
        });
    }
    function template(data)
    {
        var html = '';
        html += '<div style="border: 1px solid black">';
        html += '<div>'+data.title + '</div>';
        html += '<div>';
        html += '<img id="';
        html += 'imagePreview' + data._links.self.href.split('/')[4];
        html += '">';
        html += '</div>';
        html += '<div>' + data.text + '</div>';
        html += '<div>' + moment(data.date_created).format('YYYY-MM-DD') + '</div>';
        nid = data._links.self.href.split('/')[data._links.self.href.split('/').length - 1];
        html += '<div>' + '<a href="/vieweditnews?action=view&id=' + nid + '" target="_self">View</a>' + '</div>';
        html += '<div>' + '<a href="/vieweditnews?action=edit&id=' + nid + '" target="_self">Edit</a>' + '</div>';
        html += '</div>';
        return html;
    }
    function trimChar(string, charToRemove) {
        while(string.charAt(0)==charToRemove) {
            string = string.substring(1);
        }

        while(string.charAt(string.length-1)==charToRemove) {
            string = string.substring(0, string.length - 1);
        }

        return string;
    }
</script>