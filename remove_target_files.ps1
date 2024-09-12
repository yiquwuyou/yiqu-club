$dirs = gci . -recurse -directory -include "target"
foreach ($dir in $dirs) {
    Write-Host "Removing tracked files in $($dir.FullName)..."
    git rm --cached "$($dir.FullName)\*"
}