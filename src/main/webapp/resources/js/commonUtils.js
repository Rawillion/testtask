function getCode()
{
    var client = 'fogstream';
    var secret = 'testtask';
    return 'Basic ' + btoa(client + ':' + secret);
}

function getTokenFromCookies()
{
    return Cookies.get('fs_token');
}

function saveTokenToCookies(token)
{
    Cookies.set('fs_token', token);
}

function logout()
{
    Cookies.set('fs_token', null);
}

function redirectTo(url)
{
    // $.ajax({
    //     url: url,
    //     type: "GET",
    //     beforeSend: function(xhr) {
    //         xhr.setRequestHeader('Authorization', 'Bearer ' + getTokenFromCookies());
    //     },
    //     success: function(e) {
    //         document.write(e);
    //         // document.close();
    //     },
    //     error: function (e) {
    //         alert('Error redirect');
    //         console.log(e);
    //     }
    // });
    location.replace(url);
}

// function redirectToFree(url)
// {
//     window.location.href = url;
// }