README
======

Please, read this doc before getting started :)

Description
-----------

A applet to scan images and upload multiple images/files. Made for an ECM
project of my company, http://www.deadsimple.com.br.

It provides:

* Support for image scanning on Windows and Linux
* A lot of scanners are suportted through Twain and Sane interfaces
* Multiple image/file upload is also avaliable
* Easy to use API. Can be used in ANY webapp with minimum effort :)

Usage
-----

Just include the following code in your page. But first understand this:

* The `url_upload_pagina` is the URL your webapp has to provide to receive the files asynchronously
* `id_documento` is the id for the document or something else that "owns" the files
* `id_usuario` is the id for the user so you can authenticate it on the `url_upload_pagina`. I think you should pass a user hash or something
* Replace the word Sane for Twain if the client is running Windows!

  <applet code="SaneGedScanner.class" archive="sane_applet.jar, sane.jar" codebase="/" width="659" height="35">
    <param name="url_upload_pagina" value="http://XXX.com/document/upload"/>
    <param name="id_documento" value="34351"/>
    <param name="id_usuario" value="635"/>
    <param name="alinhamento" value="centralizado"/>

    <strong>
      Apparently you do not have
      <a href="http://www.java.com/pt_BR/download/" target="_blank">Java (version 6)</a>
      installed.
    </strong>

    <form accept-charset="UTF-8" action="http://XXX.com/document/upload?id_documento=34351&amp;id_usuario=635" enctype="multipart/form-data" method="post">
      <input id="upload" name="upload" type="file" />
      <input name="commit" type="submit" value="Enviar" />
    </form>
  </applet>

TODO
----

* Test those multifunctional scanners

Support
-------

You can contact me at mariomarroquim@gmail.com.