# Git Workflow & Branch Protection Guide

## Overview

This document outlines the enterprise-level Git workflow strategy and branch protection rules for the Student Management System project.

## Branch Structure

### Main Branches

- **`master`**: Production-ready code
- **`main`**: Alternative name for production branch (if used)
- **`testing/unit-integration-tests`**: Testing feature branch

### Feature Branch Naming Convention

```
<type>/<description>

Types:
- feature/    - New features
- bugfix/     - Bug fixes
- hotfix/     - Urgent production fixes
- test/       - Testing improvements
- docs/       - Documentation updates
- refactor/   - Code refactoring
```

**Examples:**
- `feature/user-authentication`
- `bugfix/fix-email-validation`
- `test/add-controller-tests`

## Conventional Commits

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, semicolons, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks
- **perf**: Performance improvements
- **ci**: CI/CD changes

### Examples

```bash
# Feature
feat(auth): implement JWT authentication

Add JWT-based authentication system with token refresh
mechanism and role-based access control.

Closes #123

# Bug fix
fix(validation): correct email validation regex

The previous regex was not handling special characters
correctly. Updated to follow RFC 5322 standard.

Fixes #456

# Test
test: add unit tests for StudentService

Implement comprehensive unit tests covering all CRUD
operations and edge cases.

- Test getAllStudents with empty and populated lists
- Test createStudent with duplicate email/roll
- Test updateStudent with authorization checks
```

## Git Workflow

### 1. Creating a Feature Branch

```bash
# Ensure you're on the latest main/master
git checkout master
git pull origin master

# Create and switch to feature branch
git checkout -b testing/unit-integration-tests

# Verify branch
git branch
```

### 2. Making Changes

```bash
# Check status
git status

# Stage changes
git add .

# Or stage specific files
git add src/test/java/com/sarwad/sms/studentmanagementsystem/service/StudentServiceTest.java

# Commit with conventional commit message
git commit -m "test: add unit tests for StudentService

- Implement 18 comprehensive unit tests
- Cover success cases, failure cases, and edge cases
- Use Mockito for mocking dependencies
- Follow AAA pattern"

# Push to remote
git push origin testing/unit-integration-tests
```

### 3. Creating a Pull Request

#### Via GitHub Web Interface

1. Navigate to your repository on GitHub
2. Click "Pull requests" tab
3. Click "New pull request"
4. Select base branch: `master`
5. Select compare branch: `testing/unit-integration-tests`
6. Fill in PR details:
   - **Title**: Clear, concise description
   - **Description**: What changes were made and why
   - **Link issues**: Use keywords like "Closes #123"
7. Add reviewers
8. Add labels (e.g., `enhancement`, `testing`)
9. Click "Create pull request"

#### Via Command Line (with GitHub CLI)

```bash
# Install GitHub CLI if not already installed
# https://cli.github.com/

# Create PR
gh pr create --base master --head testing/unit-integration-tests \
  --title "test: implement comprehensive testing strategy" \
  --body "## Changes
- Add 83 comprehensive tests
- Configure H2 in-memory database
- Set up GitHub Actions CI/CD

## Testing
- [x] All tests pass locally
- [x] Code follows style guidelines
- [x] Documentation updated

Closes #1"

# View PR status
gh pr status

# Check CI status
gh pr checks
```

## Branch Protection Rules

### Configuring on GitHub

#### Step 1: Navigate to Settings

1. Go to repository: `https://github.com/Rockstatata/Student-Management-System`
2. Click "Settings" tab
3. In left sidebar, click "Branches"

#### Step 2: Add Branch Protection Rule

1. Click "Add rule" or "Add branch protection rule"
2. In "Branch name pattern", enter: `master` (or `main`)
3. Enable the following settings:

##### Required Settings

✅ **Require a pull request before merging**
- Minimum number of approvals: `1`
- ✅ Dismiss stale pull request approvals when new commits are pushed
- ✅ Require review from Code Owners (if CODEOWNERS file exists)
- ✅ Require approval of the most recent reviewable push

✅ **Require status checks to pass before merging**
- ✅ Require branches to be up to date before merging
- Add status checks:
  - `test` (from GitHub Actions)
  - `build` (if configured)
  - Any other CI checks

✅ **Require conversation resolution before merging**
- Ensures all review comments are addressed

✅ **Require signed commits** (Recommended)
- Enforces GPG-signed commits for security

✅ **Require linear history** (Optional)
- Prevents merge commits, enforces rebase/squash

✅ **Include administrators**
- Apply rules to repository administrators

##### Additional Settings

✅ **Restrict pushes that create matching branches**
- Limit who can push to matching branches
- Add specific users/teams who can push

✅ **Allow force pushes** (⚠️ Disable for production)
- Keep this disabled for `master/main` branches

✅ **Allow deletions** (⚠️ Disable for production)
- Keep this disabled for `master/main` branches

#### Step 3: Save Changes

Click "Create" or "Save changes"

### Visual Guide

```
┌─────────────────────────────────────────────────────┐
│  Branch Protection Rules for 'master'               │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ✅ Require pull request before merging            │
│     └─ Required approvals: 1                        │
│     └─ Dismiss stale reviews                        │
│                                                      │
│  ✅ Require status checks to pass                  │
│     └─ test (GitHub Actions)                        │
│     └─ Require branches to be up to date            │
│                                                      │
│  ✅ Require conversation resolution                │
│                                                      │
│  ✅ Include administrators                         │
│                                                      │
│  ❌ Allow force pushes                             │
│  ❌ Allow deletions                                │
│                                                      │
└─────────────────────────────────────────────────────┘
```

## Pull Request Workflow

### 1. Creating a PR

```bash
# Push your branch
git push origin testing/unit-integration-tests

# Create PR via GitHub web interface or CLI
gh pr create
```

### 2. Code Review Process

#### For Reviewers

1. **Review Code Changes**
   - Check code quality and style
   - Verify tests are included
   - Look for security issues
   - Ensure documentation is updated

2. **Leave Comments**
   - Use inline comments for specific issues
   - Provide constructive feedback
   - Suggest improvements

3. **Approve or Request Changes**
   - ✅ Approve if changes look good
   - 🔄 Request changes if improvements needed
   - 💬 Comment without approval for questions

#### For Contributors

1. **Address Review Comments**
   - Make requested changes
   - Push new commits to the same branch
   - Respond to comments

2. **Update PR**
   ```bash
   # Make changes
   git add .
   git commit -m "fix: address review comments"
   git push origin testing/unit-integration-tests
   ```

3. **Resolve Conversations**
   - Mark conversations as resolved once addressed

### 3. CI/CD Checks

All checks must pass:
- ✅ Tests pass
- ✅ Build succeeds  
- ✅ Code quality checks pass
- ✅ No security vulnerabilities

### 4. Merging the PR

Once approved and all checks pass:

#### Option 1: Merge Commit (Default)
```bash
# Via GitHub interface
Click "Merge pull request" → "Confirm merge"
```

#### Option 2: Squash and Merge (Recommended)
```bash
# Combines all commits into one
Click "Squash and merge" → Edit commit message → "Confirm squash and merge"
```

#### Option 3: Rebase and Merge
```bash
# Rebases commits onto base branch
Click "Rebase and merge" → "Confirm rebase and merge"
```

## Merge Conflict Resolution

### Understanding Merge Conflicts

Conflicts occur when:
- Same file modified in both branches
- Same lines changed differently
- File deleted in one branch, modified in another

### Example Conflict Scenario

```bash
# Create conflict scenario
# 1. In master branch, modify README.md
git checkout master
echo "Line added in master" >> README.md
git commit -am "docs: update README in master"
git push origin master

# 2. In feature branch, modify same file
git checkout testing/unit-integration-tests  
echo "Line added in feature" >> README.md
git commit -am "docs: update README in feature"
git push origin testing/unit-integration-tests

# 3. Try to create PR → Conflict!
```

### Resolution Method 1: Via Git (Locally)

```bash
# Step 1: Update your feature branch
git checkout testing/unit-integration-tests
git fetch origin
git merge origin/master
# or
git rebase origin/master

# Step 2: Resolve conflicts
# Git will mark conflicts in files like:
# <<<<<<< HEAD
# Line added in feature
# =======
# Line added in master
# >>>>>>> origin/master

# Step 3: Edit conflicted files
# Remove conflict markers
# Keep desired changes
# Save file

# Step 4: Mark as resolved
git add README.md

# Step 5: Complete merge/rebase
git commit -m "merge: resolve conflicts with master"
# or for rebase:
git rebase --continue

# Step 6: Push changes
git push origin testing/unit-integration-tests
```

### Resolution Method 2: Via GitHub Web Interface

1. On PR page, click "Resolve conflicts"
2. GitHub opens web editor
3. Edit file to resolve conflicts:
   - Remove conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`)
   - Keep desired changes
4. Click "Mark as resolved"
5. Click "Commit merge"

### Best Practices for Conflict Resolution

1. **Keep branches updated**
   ```bash
   # Regularly sync with master
   git checkout testing/unit-integration-tests
   git fetch origin
   git rebase origin/master
   git push -f origin testing/unit-integration-tests
   ```

2. **Communicate with team**
   - Notify team of large changes
   - Coordinate on shared files

3. **Use smaller, focused PRs**
   - Easier to review
   - Fewer conflicts
   - Faster merge

4. **Choose merge vs rebase wisely**

   **When to use MERGE:**
   - ✅ Preserves complete history
   - ✅ Safe for public branches
   - ✅ Good for feature branches
   - ❌ Creates merge commits

   **When to use REBASE:**
   - ✅ Clean, linear history
   - ✅ No merge commits
   - ✅ Easier to follow
   - ❌ Rewrites history (use carefully)

## Advanced Git Workflows

### Feature Branch Workflow

```bash
# 1. Create feature branch
git checkout -b feature/new-feature master

# 2. Work on feature
git add .
git commit -m "feat: implement new feature"

# 3. Keep updated with master
git fetch origin
git rebase origin/master

# 4. Push and create PR
git push origin feature/new-feature
```

### Gitflow Workflow

```bash
# Development branch
git checkout -b develop master

# Feature from develop
git checkout -b feature/my-feature develop

# Finish feature
git checkout develop
git merge feature/my-feature

# Release branch
git checkout -b release/1.0.0 develop

# Hotfix
git checkout -b hotfix/critical-fix master
```

## Common Git Commands Reference

```bash
# Branch management
git branch                      # List branches
git branch -d branch-name       # Delete local branch
git branch -D branch-name       # Force delete
git push origin --delete branch # Delete remote branch

# Viewing history
git log --oneline --graph       # Pretty log
git log --author="John"         # Filter by author
git log --since="2 weeks ago"   # Filter by date

# Undoing changes
git reset HEAD~1                # Undo last commit (keep changes)
git reset --hard HEAD~1         # Undo last commit (discard changes)
git revert commit-hash          # Create new commit that undoes

# Stashing
git stash                       # Save changes temporarily
git stash pop                   # Apply and remove stash
git stash list                  # List stashes

# Tagging
git tag v1.0.0                  # Create tag
git tag -a v1.0.0 -m "Release"  # Annotated tag
git push origin v1.0.0          # Push tag
```

## Troubleshooting

### Can't push to protected branch

```bash
# Error: refusing to allow an OAuth App to create or update workflow
# Solution: Create PR instead
git push origin feature-branch
gh pr create
```

### PR blocked by required checks

```bash
# Wait for CI to complete
gh pr checks

# If checks fail, fix issues and push
git commit --amend
git push -f origin branch-name
```

### Need to update PR after review

```bash
# Make changes
git add .
git commit -m "fix: address review comments"
git push origin branch-name
```

## Resources

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Flow Guide](https://guides.github.com/introduction/flow/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Atlassian Git Tutorials](https://www.atlassian.com/git/tutorials)

## Support

For questions about the Git workflow, contact the development team or open a discussion on GitHub.
