//
//  InstructionsViewController.m
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 20/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import "InstructionsViewController.h"
#import "SWRevealViewController.h"

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface InstructionsViewController ()

@end

@implementation InstructionsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setupNavBar];
    
    [self initSlides];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setupNavBar{
    SWRevealViewController *revealViewController = self.revealViewController;
    if( revealViewController ){
        
        [[UINavigationBar appearance] setBarTintColor:UIColorFromRGB(0x000000)];
        [[UINavigationBar appearance] setTitleTextAttributes: [NSDictionary dictionaryWithObjectsAndKeys:
                                                               [UIColor colorWithRed:255.0/255.0 green:255.0/255.0 blue:255.0/255.0 alpha:1.0], NSForegroundColorAttributeName,
                                                               nil, NSShadowAttributeName,
                                                               [UIFont fontWithName:@"HelveticaNeue-Regular" size:16.0], NSFontAttributeName, nil]];
        
        UIButton *menuButton =  [UIButton buttonWithType:UIButtonTypeCustom];
        [menuButton setImage:[UIImage imageNamed:@"ic_launcher.png"] forState:UIControlStateNormal];
        [menuButton addTarget:self.revealViewController action:@selector( revealToggle: ) forControlEvents:UIControlEventTouchUpInside];
        [menuButton setFrame:CGRectMake(0, 0, 32, 32)];
        
        revealButtonItem = [[UIBarButtonItem alloc] initWithCustomView:menuButton];
        
        NSArray *actionLeftButtonItems = @[revealButtonItem];
        self.navigationItem.leftBarButtonItems = actionLeftButtonItems;
        
        [self.navigationController.navigationBar addGestureRecognizer: self.revealViewController.panGestureRecognizer];
    }
    
}

- (void)initSlides{
    // Create the data model
    self.pageImages = @[@"intro1.png",@"intro2.png",@"intro3.png",@"intro4.png"];
    _pageImages = self.pageImages;
    
    // Create page view controller
    self.pageViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"InstructionPageViewController"];
    self.pageViewController.dataSource = self;
    
    InstructionSlideViewController *startingViewController = [self viewControllerAtIndex:0];
    NSArray *viewControllers = @[startingViewController];
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];
    
    self.pageViewController.view.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
    
    [self addChildViewController:_pageViewController];
    [self.view addSubview:_pageViewController.view];
    [self.pageViewController didMoveToParentViewController:self];
}

#pragma mark - Page View Controller Data Source

- (InstructionSlideViewController *)viewControllerAtIndex:(NSUInteger)index{
    if (([self.pageImages count] == 0) || (index >= [self.pageImages count])) {
        return nil;
    }
    
    // Create a new view controller and pass suitable data.
    InstructionSlideViewController *pageContentViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"InstructionSlideViewController"];
    pageContentViewController.imageFile = self.pageImages[index];
    pageContentViewController.pageIndex = index;
    
    return pageContentViewController;
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController
{
    NSUInteger index = ((InstructionSlideViewController*) viewController).pageIndex;
    
    if ((index == 0) || (index == NSNotFound)) {
        return nil;
    }
    
    index--;
    return [self viewControllerAtIndex:index];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController{
    NSUInteger index = ((InstructionSlideViewController*) viewController).pageIndex;
    
    if (index == NSNotFound) {
        return nil;
    }
    
    index++;
    if (index == [self.pageImages count]) {
        return nil;
    }
    return [self viewControllerAtIndex:index];
}

- (NSInteger)presentationCountForPageViewController:(UIPageViewController *)pageViewController{
    return [self.pageImages count];
}

- (NSInteger)presentationIndexForPageViewController:(UIPageViewController *)pageViewController
{
    return 0;
}

@end
