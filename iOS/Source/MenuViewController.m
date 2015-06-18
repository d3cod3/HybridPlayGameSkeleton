//
//  MenuViewController.m
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 13/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import "MenuViewController.h"

@implementation SWUITableViewCell
@end

@interface MenuViewController ()

@end

@implementation MenuViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    nc = [NSNotificationCenter defaultCenter];
    
    // Uncomment the following line to preserve selection between presentations.
    self.clearsSelectionOnViewWillAppear = NO;
}

- (void) prepareForSegue: (UIStoryboardSegue *) segue sender: (id) sender{
    
    [self getContext];
    
    // configure the destination view controller:
    if ( [sender isKindOfClass:[UITableViewCell class]] ){
        
        //UILabel* c = [(SWUITableViewCell *)sender label];
        //UINavigationController *navController = segue.destinationViewController;
        
        //NSLog(@"%@",c.text);
    }
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 5;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    switch ( indexPath.row )
    {
        case 0:
            CellIdentifier = @"Play";
            break;
        case 1:
            CellIdentifier = @"Sensor";
            break;
        case 2:
            CellIdentifier = @"Instructions";
            break;
        case 3:
            CellIdentifier = @"Credits";
            break;
        case 4:
            CellIdentifier = @"Exit";
            break;
    }
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier: CellIdentifier forIndexPath: indexPath];
    
    UIView *bgColorView = [[UIView alloc] init];
    bgColorView.backgroundColor = [UIColor redColor];
    [cell setSelectedBackgroundView:bgColorView];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    
    if([cell.textLabel.text isEqualToString:@"Exit"]){
        exit(0);
    }
    
}

#pragma mark - Core Data

- (void)getContext{
    appDelegate = [HPAppDelegate sharedAppDelegate];
    context = appDelegate.managedObjectContext;
}

#pragma mark state preservation / restoration
- (void)encodeRestorableStateWithCoder:(NSCoder *)coder {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    // TODO save what you need here
    
    [super encodeRestorableStateWithCoder:coder];
}

- (void)decodeRestorableStateWithCoder:(NSCoder *)coder {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    // TODO restore what you need here
    
    [super decodeRestorableStateWithCoder:coder];
}

- (void)applicationFinishedRestoringState {
    NSLog(@"%s", __PRETTY_FUNCTION__);
    
    // TODO call whatever function you need to visually restore
}

// -----------------------------------------------------------------------------

@end
