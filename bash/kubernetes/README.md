## How to use

1. Check out this repo

```
git clone git@github.com:levinsamuel/rand.git
```

2. Link this file to your home directory

```
cd ~
ln -s path/to/this/repo/rand/bash/kubernetes/.k8src .
```

3. Source this file in your profile script

```
# In ~/.bash_profile, ~/.profile, ~/.bashrc, whichever
. "${HOME}/.k8src"
```

4. When you update the repo it will update your source.
