import os

def remove_file(directory, filetype):
    # Add your aboslute path
    for root, dirs, files in os.walk(directory):
        for item in files:
            # Change '.txt' to the file extension type you're doing (i.e. '.java')
            if not item.endswith(filetype):
                print("Deleting", item, "...")
                pre, ext = os.path.splitext(item)
                os.remove(root+'\\'+item)

def convert_file(directory, filetype):
    # Add your aboslute path
    for root, dirs, files in os.walk(directory):
        for item in files:
            # Change '.txt' to the file extension type you're doing (i.e. '.java')
            if item.endswith(filetype):
                print("Changing filetype: ", root+item, "...")
                pre, ext = os.path.splitext(root+item)
                os.rename(root+'\\'+item, pre+'.txt')

def delete_empty_directory(directory):
    crawling = True
    while crawling:
        crawling = False
        for root, dirs, files in os.walk(directory):
            if files == [] and dirs == []:
                print("Removing Empty Directory:", root)
                os.rmdir(root)
                crawling = True 
        print()

if __name__ == "__main__":
    print("This program will go through every directory and sub-directory, converting specified file format (i.e. .java, .py)\n into .txt files. This program will also delete ALL files that are not of the specified file format type. \nLastly, this program will delete all empty subdirectory as a clean up at the end.\n Yes, overall it's a 2/10 user experience, I know.")
    
    directory = input("Enter the ABSOLUTE directory path: ")
    
    filetype = input("Enter your extension file type:\n1)Java\n2)Python\n3)Others (type in .sql for a sql file for example)\n")

    if filetype == '1':
        filetype = '.java'
    elif filetype == '2':
        filetype = '.py'
        
    check = input("Please make sure you back up ALL your files as this action is irreversible. Proceed (y/n)?: ")
    
    if check == 'y':
        remove_file(directory, filetype)
        print()
        convert_file(directory, filetype)
        print()
        delete_empty_directory(directory)
        print()
        print("Task Completed. Ez MDP.")




