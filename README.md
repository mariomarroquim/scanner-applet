README
======

Please, read this doc before getting started :)

Description
-----------

A applet to scan images and upload multiple images/files. Made for an ECM project..

It provides:

* Support for image scanning on Windows and Linux.
* A lot of scanners are suportted through Twain and Sane interfaces.
* Multiple image/file upload is also avaliable.
* Easy to use API. Can be used in ANY webapp with minimum effort :)

Usage
-----

Just include [this code](http://www.pastie.org/3334867) in your page. But first understand this:

* The `url_upload_pagina` is the URL your webapp has to provide to receive the files asynchronously. Each file will be received in the `upload` param.
* `id_documento` is the id for the document or something else that "owns" the files.
* `id_usuario` is the id for the user so you can authenticate it on the `url_upload_pagina`. I think you should pass a user hash or something.
* Replace the word "Sane" for "Twain" if the client is running Windows!
* Look into the code for more documentation :)

TODO
----

* Test those multifunctional scanners!

Support
-------

You can contact me at mariomarroquim@gmail.com.

