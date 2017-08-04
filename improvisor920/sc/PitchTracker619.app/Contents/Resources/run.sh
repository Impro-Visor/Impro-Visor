echo "includePaths:
    - overwrites
    - SCClassLibrary
excludePaths:
    - $HOME/Library/Application Support/SuperCollider/Extensions
    - /Library/Application Support/SuperCollider/Extensions
    - /Applications/SuperCollider.app/Contents/Resources/SCClassLibrary
postInlineWarnings: false" > langconf.yaml;
./Resources/sclang -llangconf.yaml simplePitchTrack.scd
