README
======

Estas instruções são para funcionamento do applet de digitalização do GED e foram testadas
em várias versões do Ubuntu. Será necessário algum conhecimento de Linux, mas este guia lhe ajudará
nas terefas mais difíceis. Em caso de dúvidas, mande um email para mariomarroquim@gmail.com.

1 - Atualize o apt-get com o comando "apt-get update".
2 - Instale o Java e certifique-se de que esteja funcionando com o comando "java -version".
3 - Instale as bibliotecas do SANE, incluíndo todos os pacotes "libsane*" e "sane-utils" (teste se o 
    scanner está funcionando através de algum aplicativo gráfico no menu).
4 - Instale o ImageMagick.
5 - Copie os arquivos "digitalizar_colorido.sh" e "digitalizar_preto_e_branco.sh" para a pasta "/usr/bin".
    Torne eles executáveis com o comando "chmod 777 <nome_do_arquivo>".