# Projecto de Sistemas Distribuídos #

## Primeira entrega ##

Grupo de SD 49

Guilherme Andrade, 77955, g.andrade.1995@gmail.com

Tiago Martins, 78082, tiago.mousinho.martins@gmail.com

Rui Ferreira, 77935, rui_fs_ferreira@hotmail.com



Repositório:
[tecnico-softeng-distsys-2015/T_00_00_49-project] (https://github.com/tecnico-softeng-distsys-2015/T_00_00_49-project)


-------------------------------------------------------------------------------

## Serviço SD-STORE 


### Instruções de instalação 

[0] Iniciar sistema operativo

Linux

[1] Iniciar servidores de apoio

JUDDI:
> sh startup.sh

[2] Criar pasta temporária

> cd Desktop
> mkdir projSD
> cd projSD

[3] Obter versão entregue

> git clone https://github.com/tecnico-softeng-distsys-2015/T_00_00_49-project


[4] Construir e executar SD-Store (servidor)

> cd T_00_00_49-project/sd-store
> mvn generate-sources
> mvn compile
> mvn exec:java


[5] Construir SD-Store-cli (cliente)

> cd Desktop/projSD/T_00_00_49-project/sd-store-cli  
> mvn generate-sources
> mvn compile




-------------------------------------------------------------------------------

### Instruções de teste: ###

[1] Executar SD-Store (test)

> cd Desktop/projSD/T_00_00_49-project/sd-store  
> mvn test


[2] Executar SD-Store-cli (test)

> cd Desktop/projSD/T_00_00_49-project/sd-store-cli  
> mvn test

-------------------------------------------------------------------------------
**FIM**
