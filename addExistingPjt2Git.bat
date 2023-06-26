@echo off
setlocal
:PROMPT
rem Git URL eg:  https://userid@bitbucket.org/userid/repository-name.git
set /p remoteRepositoryURL="Enter remote repository URL: or 'q' to quit "
echo "Entered remote repository URL was " %remoteRepositoryURL%
set quitbat=true
IF not "%remoteRepositoryURL%" == "q" IF not "%remoteRepositoryURL%" ==  "Q" set quitbat=false
if "%quitbat%" == "true" goto END
:NOTEND
rem echo "Inside :NOTEND Entered Value was " %ANYKEY%
git init
git add .
git commit -m "First commit"
git remote add origin %remoteRepositoryURL%
echo # list of files and folders to ignore in git  >> .gitignore
rem for %%f IN (.gitignore,addExistingPjt2Git.bat)do echo %%f >> .gitignore
git remote -v
git pull --progress -v --no-rebase --allow-unrelated-histories "origin" main
git push origin main
set /p ANYKEY="Successfully added project to GIT, Press any key to continue: "
:END
endlocal