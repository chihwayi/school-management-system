#!/bin/bash

# Release script for School Management System

if [ $# -eq 0 ]; then
    echo "Usage: $0 <version> [message]"
    echo "Example: $0 1.0.0 'Initial release'"
    exit 1
fi

VERSION=$1
MESSAGE=${2:-"Release version $VERSION"}

echo "🚀 Creating release v$VERSION..."

# Check if we're on main branch
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo "❌ Error: You must be on the main branch to create a release"
    echo "Current branch: $CURRENT_BRANCH"
    exit 1
fi

# Check if working directory is clean
if [ -n "$(git status --porcelain)" ]; then
    echo "❌ Error: Working directory is not clean"
    echo "Please commit or stash your changes before creating a release"
    exit 1
fi

# Create and push tag
echo "📝 Creating tag v$VERSION..."
git tag -a "v$VERSION" -m "$MESSAGE"

echo "📤 Pushing tag to remote..."
git push origin "v$VERSION"

echo "✅ Release v$VERSION created successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Check GitHub Actions for deployment status"
echo "2. Create a GitHub release with release notes"
echo "3. Update documentation if needed"
