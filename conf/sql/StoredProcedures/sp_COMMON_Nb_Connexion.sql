if exists (select 1 from sysobjects where id = object_id('sp_COMMON_Nb_Connexion') and type = 'P')
begin
   drop proc sp_COMMON_Nb_Connexion
   print 'Procedure sp_COMMON_Nb_Connexion supprimee'
end
go
          
/****************************************************************/
/* Procédure permettant de déterminer le nombre de connexion	*/
/* par utilisateur et application.                    			*/
/****************************************************************/

create proc sp_COMMON_Nb_Connexion (@USER_NAME varchar(30), @APPLICATION_NAME varchar(16),
       @CATALOG_NAME  varchar(30)) as
begin      
             select count(suid) from master..sysprocesses
                          where suser_name(suid) = @USER_NAME
                          and program_name like @APPLICATION_NAME
                          and db_name(dbid) = @CATALOG_NAME
end
go


if exists (select 1 from sysobjects where id = object_id('sp_COMMON_Nb_Connexion') and type = 'P')
   print 'Procedure sp_COMMON_Nb_Connexion cree'
go
